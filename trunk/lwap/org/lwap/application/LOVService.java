/**
 * Created on: 2004-3-29 14:24:28
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.application.event.SessionController;
import org.lwap.application.fnd.SessionInitializer;
import org.lwap.controller.MainService;
import org.lwap.database.DatabaseQuery;
import org.lwap.database.SQLSelectStatement;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.mvc.Layout;
import org.lwap.mvc.LinkView;
import org.lwap.mvc.ViewFactoryStore;
import org.lwap.mvc.servlet.JspViewFactory;
import org.lwap.ui.UIAttribute;

import uncertain.composite.CompositeMap;

/**
 * Implements LOV feature
 * 1. create query statement
 * 2. create query form
 * 3. create result display table
 */
public class LOVService extends MainService {

	public static final String PATH_CAN_ACCESS_LOV_VALUE = "/model/CAN_ACCESS_LOV/@VALUE";
    /* lov properties */	
	public static final String KEY_LOV_CONFIG = "lovconfig";
	public static final String KEY_QUERY_SOURCE = "QuerySource";
	public static final String KEY_QUERY_STATEMENT = "QueryStatement";

	/* field properties */
	public static final String KEY_NAME 			= "Name";
	public static final String KEY_PROMPT 		= "Prompt";
	public static final String KEY_RETURN_FIELD 	= "ReturnField";
	public static final String KEY_FOR_DISPLAY	= "ForDisplay";
	public static final String KEY_FOR_QUERY		= "ForQuery";
	public static final String KEY_ORDER			= "Order";
	public static final String KEY_RETURN_DISPLAY = "ReturnDisplay";
	public static final String KEY_RETURN_VALUE	= "ReturnValue";

	
	CompositeMap	lov_config = null;
	LinkedList		query_fields 	= new LinkedList(),
					order_fields 	= new LinkedList(),
					display_fields	= new LinkedList();
					//return_fields   = new LinkedList();

	CompositeMap	display_field = null, value_field = null;
					
	public void initLovConfig(String config_name) throws IOException, org.xml.sax.SAXException{
			lov_config = this.application.getCompositeLoader().load(config_name);
            lov_config.setName("lov");
            this.getServiceContext().addChild(lov_config);
			Iterator it = lov_config.getChildIterator();
			if( it == null) throw new IllegalArgumentException("LOV config error: no lov field");		
			while(it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				if( field.getBoolean(KEY_FOR_QUERY, false))
					query_fields.add(field);
				if( field.containsKey(KEY_ORDER)) 
					order_fields.add(field);
				if( field.getBoolean(KEY_FOR_DISPLAY, true)) 
					display_fields.add(field);
				if( field.getBoolean(KEY_RETURN_DISPLAY, false))
					display_field = field;
				if( field.getBoolean(KEY_RETURN_VALUE, false))
					value_field = field;					
				/*
				if( field.containsKey(KEY_RETURN_FIELD)) 
					return_fields.add(field);					
					*/
			}
			if( display_field == null) throw new IllegalArgumentException("LOV config error: no display field");
			if( value_field == null) throw new IllegalArgumentException("LOV config error: no value field");
	}
	
	
	public void createQueryStatement(){
		String query_str = lov_config.getString(KEY_QUERY_STATEMENT);
		if( query_str == null){
			String query_source = lov_config.getString(KEY_QUERY_SOURCE);
			if( query_source == null) throw new IllegalArgumentException("LOV config error: neither QueryStatement nor QuerySource specified");
			
			SQLSelectStatement query = new SQLSelectStatement(query_source);
			
			Iterator it = lov_config.getChildIterator();
			while( it.hasNext()){
				CompositeMap field = (CompositeMap)it.next();
				query.addField(field.getString(KEY_NAME));				
			}
			
			query_str = query.getSQL();
			query_str += (" " + QueryBasedService.KEY_WHERE_TAG );
			if(order_fields.size()>0){
				query_str +=" order by ";			
				it = order_fields.iterator();
				int count = 0;
				while( it.hasNext()){
					CompositeMap field = (CompositeMap)it.next();
					if( count>0) query_str += ",";
					query_str +=field.getString(KEY_NAME) + " " + field.getString(KEY_ORDER);
					count++;
				}
			}
			
		}	
		
		CompositeMap query_config = getModelConfig().getChild(QueryBasedService.KEY_QUERY_STATEMENT);
		if(query_config == null) throw new IllegalArgumentException("no query-statement in service config");
		query_config.put("Sql", query_str);
		
		if( lov_config.containsKey(QueryBasedService.KEY_DEFAULT_WHERE))
			query_config.put(QueryBasedService.KEY_DEFAULT_WHERE, lov_config.get(QueryBasedService.KEY_DEFAULT_WHERE));
		
		boolean page_resultset = lov_config.getBoolean(DatabaseQuery.KEY_PAGE_RESULTSET, false);
		if( page_resultset){
			Integer pagesize = lov_config.getInt(DatabaseQuery.KEY_PAGE_SIZE);
			if(pagesize==null) pagesize = new Integer(20);
			query_config.put(DatabaseQuery.KEY_PAGE_RESULTSET, "true");
			query_config.put(DatabaseQuery.KEY_PAGE_SIZE, pagesize);
			CompositeMap page_control = getServiceConfig().getChild("page-control");
			if( page_control != null){
				page_control.put(DatabaseQuery.KEY_PAGE_SIZE, pagesize);
				getViewConfig().getChilds().add(1, page_control);
				page_control.setParent(getViewConfig());
			}
		}
		
	}
	
	public void createQueryForm(){
		ViewFactoryStore store = getViewBuilderStore();
		CompositeMap form_layout = (CompositeMap)getViewConfig().getObject("form/layout");
		if(form_layout == null) throw new  IllegalArgumentException("no layout exists in form config");
		Iterator it = query_fields.iterator();
		while( it.hasNext()){
			CompositeMap field = (CompositeMap)it.next();
			CompositeMap prompt = store.createView("textlabel");
			prompt.put(DataBindingConvention.KEY_DATAVALUE, this.getLocalizedString(field.getString(UIAttribute.ATTRIB_PROMPT)));
			form_layout.addChild(prompt);
			
			CompositeMap control = JspViewFactory.createView("input");
			String fname = field.getString(KEY_NAME);
            control.put(KEY_NAME, fname);
            String query_where = field.getString(QueryBasedService.KEY_QUERY_WHERE);
            if(query_where!=null)
                control.put(QueryBasedService.KEY_QUERY_WHERE, query_where);
            else
                //control.put("Operator", "like");
                control.put(QueryBasedService.KEY_QUERY_WHERE, fname+" like '%'||${@"+fname+"}||'%'");
			control.put("Type", "textedit");
			String inputsize = field.getString("InputSize");
			if( inputsize != null) control.put("InputSize", inputsize);
/*
			form_layout.getChilds().add(form_layout.size(),control);
			control.setParent(form_layout);
*/
			form_layout.addChild(control);						
		}
		if( !form_layout.containsKey(Layout.KEY_COLUMNS))
			form_layout.put(Layout.KEY_COLUMNS, new Integer(form_layout.getChilds().size()));	
	}
	
	public CompositeMap createLinkContent(String fld_name){
		CompositeMap link = getViewBuilderStore().createView("link");
		String disp_fld_name = display_field.getString(KEY_NAME);
		if( disp_fld_name == null) throw new IllegalArgumentException("LOV: must specify name for display field");
		String value_fld_name = value_field.getString(KEY_NAME);
		if( value_fld_name == null) throw new IllegalArgumentException("LOV: must specify name for value field");
		
		String href = "javascript:opener.set_return_value('${@"+value_fld_name+"}','${@"+disp_fld_name+"}'); window.close();";
//		String returnFunction = lov_config.getString("ReturnFunction", "");
		String returnFunction = "parent.commit";
		if(!"".equals(returnFunction)) {
			href = "javascript:if("+returnFunction+") { "+returnFunction+"('${@"+value_fld_name+"}','${@"+disp_fld_name+"}'); }else{opener.set_return_value('${@"+value_fld_name+"}','${@"+disp_fld_name+"}'); window.close();}";
		}
		link.put(LinkView.KEY_HREF, href);
		link.put(LinkView.KEY_CONTENT, "${@" + fld_name + "}");
		return link;
	}
	
	public void createTable(){
		CompositeMap table = (CompositeMap)getViewConfig().getChild("table");
		for( int i=0; i<display_fields.size(); i++){
			CompositeMap field = (CompositeMap)display_fields.get(i);
			String name = field.getString(KEY_NAME);
			CompositeMap column = table.createChild("column");
			column.put(DataBindingConvention.KEY_DATAFIELD, "@"+name);
			column.put(UIAttribute.ATTRIB_PROMPT, field.get(UIAttribute.ATTRIB_PROMPT));
			if( field.containsKey(UIAttribute.ATTRIB_WIDTH))
				column.put(UIAttribute.ATTRIB_WIDTH, field.get(UIAttribute.ATTRIB_WIDTH));
			if( i==0) column.addChild(createLinkContent(name));		
		}
		
	}

	/**
	 * @see org.lwap.application.BaseService#preService()
	 */
    /*
	public void preService() throws IOException, ServletException {
		super.preService();
		try{
			String config_name = getParameters().getString(KEY_LOV_CONFIG);
			initLovConfig(config_name);
			createQueryStatement();
			createQueryForm();
			createTable();
		}catch(org.xml.sax.SAXException ex){
			throw new ServletException("Error parsing lov config file", ex);
		}
	}
    */
    
    boolean checkLovAccess()
        throws ServletException
    {
        String function_code = lov_config.getString("FunctionCode");
        if(function_code==null)
            return true;
        CompositeMap access_def = getServiceConfig().getChild( BaseService.KEY_ACCESS_CHECK );
        if(access_def==null) return true;
        //System.out.println("begin check");
        databaseAccess(access_def, getParameters(), getModel());
        //System.out.println(this.getServiceContext().toXML());
        Object obj = this.getServiceContext().getObject(PATH_CAN_ACCESS_LOV_VALUE);
        if(obj!=null){
            if("1".equals(obj.toString()))
                return true;
            else
                return false;
        }else
            throw new IllegalArgumentException("This statement return no result at path "+PATH_CAN_ACCESS_LOV_VALUE+":"+access_def.toXML());
    }
    
    public void onCheckAccess() throws ServletException{
        if(!checkLovAccess()){
            SessionController sc = SessionController.createSessionController(getServiceContext());
            sc.setContinueFlag(false);
            String url = SessionInitializer.getNoAccessURL(this, "${/lov/@FunctionCode}" );
            sc.setDispatchUrl(url);
        }
    }

    /**
     * @see org.lwap.controller.MainService#service(javax.servlet.http.HttpServlet, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void service(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try{
            super.doinit(servlet, request, response);
            String config_name = request.getParameter(KEY_LOV_CONFIG) ;
            if(config_name==null) throw new IllegalArgumentException(KEY_LOV_CONFIG + " is null ");
            initLovConfig(config_name);
            /*
            if(!checkLovAccess()){
                String url = SessionInitializer.getNoAccessURL(this, "${/lov/@FunctionCode}" );
                response.sendRedirect(url);
                return;
            }
            */
            createQueryStatement();
            createQueryForm();
            createTable();
        }catch(org.xml.sax.SAXException ex){
            throw new ServletException("Error parsing lov config file", ex);
        }        
        super.service(servlet, request, response);
    }
    
    

}
