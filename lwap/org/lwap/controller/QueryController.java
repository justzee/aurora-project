/**
 * Created on: 2002-11-17 15:59:18
 * Author:     zhoufan
 */
package org.lwap.controller;


import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lwap.application.BaseService;
import org.lwap.database.DatabaseQuery;
import org.lwap.database.WhereClause;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.ui.UIAttribute;
import org.lwap.ui.web.Form;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

/** Notice: use lower case of "select" and "from" in query-statement!
 */

public class QueryController extends AbstractController  implements IFeature {

	public static final String KEY_AUTO_QUERY = "auto-query";
	public static final String KEY_QUERY_STATEMENT = "query-statement";
	public static final String KEY_OPERATOR = "Operator";
	public static final String KEY_WHERE_TAG = "#WHERE_CLAUSE#";
	public static final String KEY_DEFAULT_WHERE = "DefaultWhere";
    public static final String KEY_QUERY_WHERE = "QueryWhere";
    public static final String KEY_AUTO_COUNT = "AutoCount";    
    public static final String KEY_COUNT_FIELD = "CountField";
    public static final String KEY_COUNT_ELEMENT = "CountElement";
    public static final String KEY_FOR_QUERY = "ForQuery";
    public static final String KEY_SQL_TYPE = "SqlType";
    public static final String KEY_QUERY_POPULATED = "__query_populated__";
    
    
    CompositeMap	queryConfig;
    CompositeMap	formConfig;
    FormController  formController;
    Configuration procConfig;
    String          parentName;

    /**
     * @param engine
     */
    public QueryController(UncertainEngine engine) {
        super(engine);
    }

    /* (non-Javadoc)
     * @see uncertain.proc.IFeature#onAttach(uncertain.composite.CompositeMap, uncertain.event.Configuration)
     */
    public int attachTo(CompositeMap config, Configuration procConfig) {
        this.queryConfig = config;
        this.procConfig = procConfig;
        return IFeature.NORMAL;
    }
    
    public int detectAction(HttpServletRequest request, CompositeMap context) {
        return IController.ACTION_NOT_DETECTED;
    }
    
    public void prePrepareService(ProcedureRunner runner) throws Exception {
        String name = queryConfig.getString("Name");
        CompositeMap view = ServiceInstance.getViewConfig();
        if(name!=null){
            formConfig = CompositeUtil.findChild(view,"form","Name",name);
            if(formConfig==null) throw new ConfigurationError("Can't find form "+name+" for query statement");
        }else{
            formConfig = view.getChild("form");
            if(formConfig==null) throw new ConfigurationError("no query form in view");
        }       
        // get form controller
		formController = (FormController)procConfig.getFeatureInstance(formConfig, FormController.class);
        Form the_form = (Form) DynamicObject.cast(formConfig,Form.class);
		// create default form parameter
        // the_form.createParameter("pagesize",Long.class,false,"30");
		// the_form.createParameter("pagenum",Long.class,false,"1");
		// set default cache = true
		CompositeMap config = ServiceInstance.getServiceConfig();
		Object cache = config.get(BaseService.KEY_CACHE);
		if(cache==null) config.put(BaseService.KEY_CACHE, new Boolean(true));
        
        parentName= queryConfig.getParent().getName();
    
        formController.setAllowRepeatSubmit(true);
    }
    
    
	protected WhereClause getWhereClause( Form frm, CompositeMap parameters ){
		WhereClause where = new WhereClause();
		Map params = frm.getInputFields();
		Iterator it = params.values().iterator();
		while( it.hasNext()){
			CompositeMap input_fld = (CompositeMap)it.next();
			boolean forQuery = input_fld.getBoolean(KEY_FOR_QUERY, true);
			if(!forQuery) continue;
			String name = UIAttribute.getName(input_fld);
			if( parameters.containsKey(name) ){
				StringBuffer expr = new StringBuffer();
                String queryWhere = input_fld.getString(KEY_QUERY_WHERE);
				String operator = input_fld.getString(KEY_OPERATOR);
				
                if (queryWhere != null)
                   where.addWhereClause(queryWhere);
                else if(operator != null){
					expr.append(name).append(' ').append(operator).append(" ${").append(input_fld.getString(DataBindingConvention.KEY_DATAFIELD) ).append('}');
					where.addWhereClause(expr.toString());
                }
			}
		}
		return where;
	}

	protected void buildQuery() throws Exception {

		boolean is_auto_query = ServiceInstance.getServiceConfig().getBoolean(KEY_AUTO_QUERY,false);
		boolean is_page_in_post = FormController.FORM_POST.equals(ServiceInstance.getRequest().getParameter(FormController.KEY_FORM_STATE));
		if( formController.isFormPost() || is_auto_query ){

			Form form = formController.getForm();
			if(!FormController.isInputValid(ServiceInstance.getServiceContext())){
			    return;
			}
            

            
				// create query statement
				DatabaseQuery query = (DatabaseQuery)DynamicObject.cast( queryConfig, DatabaseQuery.class);
				if( Boolean.TRUE.equals(query.get(KEY_QUERY_POPULATED)))
                    return;
                
                String sql = query.getSql();
				if( sql==null) 
                    throw new IllegalArgumentException("QueryBasedService:must specify 'Sql' property");
				String default_where = query.getObjectContext().getString(KEY_DEFAULT_WHERE);
				WhereClause where = getWhereClause( formController.getForm(), ServiceInstance.getParameters());
				if( default_where != null) where.addWhereClause(default_where);
				StringBuffer buf = new StringBuffer(sql);
				int idx = buf.indexOf(KEY_WHERE_TAG);
				if( idx<0) {
                    throw new ConfigurationError("QueryController: #WHERE_CLAUSE# not set in query statement "+queryConfig.toXML());
                }
				buf.replace(idx,idx+KEY_WHERE_TAG.length(),where.getFullStatement());
				sql = buf.toString();
				query.setSql(sql);
                String sql_type = queryConfig.getString(KEY_SQL_TYPE);
                if(sql_type!=null)
				    query.setAccessType(sql_type);
                else
                    query.setAccessType(DatabaseQuery.QUERY);
				
				// create select count statement
				boolean auto_count = query.getBoolean(KEY_AUTO_COUNT, true);
				if( auto_count && sql_type==null ){
					String count_field = query.getString("CountField", "TOTAL_COUNT");
					String count_element = query.getString(KEY_COUNT_ELEMENT, "query-count");
                    StringBuffer count_sql = new StringBuffer();
                    count_sql.append("select count(1) as ").append(count_field).append(" from ( ").append(sql).append(")");
                    DatabaseQuery count_query = DatabaseQuery.createQuery(count_sql.toString());
                    count_query.setPageResultset(false);
                    count_query.setElementName(count_element);
                    ServiceInstance.getModelConfig().addChild(count_query.getObjectContext());
                    //System.out.println(ServiceInstance.getModelConfig().toXML());
					/*
                    StringBuffer count_sql = new StringBuffer(sql);
					int index_from = count_sql.indexOf("select");
					int index_to   = count_sql.indexOf("from")+1;
					if( index_from>=0 && index_to>7){
						count_sql.replace(index_from+7,index_to-1, " count(*) as "+count_field+" ");
						DatabaseQuery count_query = DatabaseQuery.createQuery(count_sql.toString());
						count_query.setPageResultset(false);
						count_query.setElementName("query-count");
						ServiceInstance.getModelConfig().addChild(count_query.getObjectContext());
					}else
						uncertainEngine.getLogger().warning(ServiceInstance.getServiceName()+": can't create count statement from query sql. Hint: 'select' and 'from' must be lower case.\r\n Original sql:"+sql);
                        */
				}
                
                query.putBoolean(KEY_QUERY_POPULATED, true);

		}

	}
    
    public void preCreateModel(ProcedureRunner runner) 
    throws  Exception {
        if("model".equals(parentName))
            buildQuery();
    }
	
	public void postCreateView(){
        //System.out.println(ServiceInstance.getModel().toXML());
	    if(formController!=null){
	        Form the_form = formController.getForm();
			String str = the_form.getString(Form.KEY_SUBMIT_PROMPT);
			// set submit button prompt
			if (str == null) the_form.putString(Form.KEY_SUBMIT_PROMPT, ServiceInstance.getLocalizedString("prompt.query"));
			// populate field with input
	        if(formController.isFormPost()){
	            formController.getForm().populateFormWithInput(ServiceInstance.getParameterSource());
	        }
	    }
	}

	public void onPostDone(ProcedureRunner runner){
	    // generate UI on form post done
	    CompositeMap context = ServiceInstance.getServiceContext();
	    if(formController.isFormPost() && FormController.isInputValid(context)){	        
	        context.put(MainService.KEY_VIEW_OUTPUT, new Boolean(true));
	        ServiceInstance.setNextProcedure(ControllerProcedures.GENERATE_UI);
	    }
	}
	
	public void preDoAction(ProcedureRunner runner) throws Exception {
        /*
        buildQuery();
	   */
        // stop FormController from do action
        /*
	    if( formController.isFormPost() ){
	        runner.setHandleFlag(uncertain.event.EventModel.HANDLE_STOP);
	    }
        */
        if("action".equals(parentName)){
            buildQuery();
            //System.out.println("query statement: "+queryConfig.getString("Sql"));
        }
        
	}


}
