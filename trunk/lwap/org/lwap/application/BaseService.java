/**
 * Created on: 2002-11-11 11:11:14
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.sql.DataSource;

import org.lwap.controller.StateFlag;
import org.lwap.database.DatabaseAccess;
import org.lwap.mvc.ClassViewFactory;
import org.lwap.mvc.Layout;
import org.lwap.mvc.LocalizedStringProvider;
import org.lwap.mvc.ViewFactoryStore;
import org.lwap.mvc.ViewPopulate;
import org.lwap.mvc.servlet.JspViewFactory;
import org.lwap.mvc.servlet.ServletBuildSession;
import org.lwap.validation.InputParameterImpl;
import org.lwap.validation.ParameterParser;
import org.lwap.validation.ParameterSource;
import org.lwap.validation.RequestParameterSource;
import org.lwap.validation.ValidationException;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

public class BaseService  extends ServiceImpl implements LocalizedStringProvider {
	
	public static final String KEY_PRE_SERVICE = "pre-service";
    public static final String KEY_TEMPLATE = "template";
    public static final String KEY_MODEL = "model";
	public static final String KEY_VIEW  = "view";
	public static final String KEY_ACTION = "action";
	public static final String KEY_SESSION  = "session";
	public static final String KEY_PARAMETER  = "parameter";
	public static final String KEY_SESSION_LOCALE = "session-locale";

	public static final String KEY_ACCESS_CHECK = "access-check";	
	public static final String KEY_ACCESS_TESTFIELD = "test-field";
	public static final String KEY_ACCESS_TESTVALUE = "test-value";
	public static final String KEY_MULTI_LANGUAGE = "multi-language";	
		
	public static final String KEY_SERVICE_OUTPUT = "service-output";
	public static final String KEY_PAGE_TEMPLATE = "page-template";
	public static final String KEY_DEFAULT_PAGE_TEMPLATE = "default-page-template";
	public static final String KEY_DEFAULT_DISPATCH = "default-dispatch";
	public static final String KEY_TARGET_TYPE = "target-type";
	public static final String KEY_DISPATCH_STYLE = "dispatch-style";
	public static final String KEY_CACHE = "cache";
	
    public static final String KEY_POPULATE_CLASS  = "populate-class";
    public static final String KEY_POPULATE_SOURCE = "populate-source"; 
    
	public static final String KEY_CALLING_SERVICE_NAME = "CALLING_SERVICE_NAME";   
	
	protected static  ArrayList page_parameters = new ArrayList(2);
	
	static{
		page_parameters.add( InputParameterImpl.createParameter("pagesize",Long.class,true,new Long(30)));
		page_parameters.add( InputParameterImpl.createParameter("pagenum",Long.class,true,new Long(1)));
	}
	
	boolean						dumped = false;
	boolean     				_view_output = false;
	protected RequestParameterSource	parameter_source;	
	
	
	public class ViewPopulateIterator implements IterationHandle {
		
    	public int process( CompositeMap map){
    		String cls = map.getString(KEY_POPULATE_CLASS);
    		if( cls != null){
    			CompositeMap model = getModel();
    			String src = map.getString(KEY_POPULATE_SOURCE);
    			if( src != null && model != null)
    				model = (CompositeMap)model.getObject(src);
    				
    			ViewPopulate vp = (ViewPopulate) application.getPooledObject(cls);
    			if( vp != null) 
	    			try{
	    			   vp.populateView(model,map);
	    			} catch(Throwable thr){
	    				thr.printStackTrace();
	    			}
	    		else
	    			System.err.println("Warning: populate-class attribute specified in view, but can't create instance of "+ cls);	
	
    		}
    		
    		return IterationHandle.IT_CONTINUE;
    	}
		
	};

	
/* ------------------ BEGIN Access application scope objects -------------------------------- */
	
	public Object getApplicationParameter(String key){
		return getApplicationConfig().get(key);
	}
	
	
	public DataSource getDataSource(){
		//return (DataSource)getApplicationConfig().get( FacadeServlet.KEY_DATA_SOURCE);
		return this.application.getDataSource();
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = getDataSource().getConnection();
		if(conn==null)
		    throw new SQLException("Can't get connection");
	    return conn; 
	}
		
	public ViewFactoryStore getViewBuilderStore(){
		//return (ViewBuilderStore)getApplicationConfig().get(FacadeServlet.KEY_VIEW_BUILDER_STORE);
		return this.application.getViewBuilderStore();
	}	
	
	public ServletContext getServletContext(){
		//return ((WebApplication)getApplication()).getServletContext();
		return this.application.getServletContext();
	}

/* ------------------ END Access application scope objects -------------------------------- */
	

/* ------------------ BEGIN Access service configuration -------------------------------- */

	/** get a parameter from service config */
	public Object getServiceParameter(String key){
		return getServiceConfig().get(key);
	}
	
	/** get a sub section from service config 
	 *  @param section_name name of section, such as "model", "view"
	 */
	public CompositeMap getServiceConfigSection(String section_name){	
		return getServiceConfig().getChild(section_name);
	}

	/** get model config of this service */
	public CompositeMap getModelConfig(){
		return getServiceConfigSection(KEY_MODEL);
	}

	/** get view config of this service */	
	public CompositeMap getViewConfig(){
		return getServiceConfigSection(KEY_VIEW);
	}
	
	public String getActionName(){
	    String action_name = request.getParameter(StateFlag.KEY_ACTION_FLAG);
	    if(action_name!=null)
	        if(action_name.length()==0) action_name= null;
        return action_name;    
	}
	
	/** get action config of this service */	
	public CompositeMap getActionConfig(){	    
	    CompositeMap rtn = null;
	    rtn = getServiceConfig().getChildByAttrib(KEY_ACTION,"Name",getActionName());
	    return rtn;
	}
	
	/** get parameter config of this service */
	public CompositeMap getParameterConfig(){
		return getServiceConfigSection(KEY_PARAMETER);
	}

	public void setHttpObject(HttpServletRequest request,	HttpServletResponse response ){
		super.setHttpObject(request,response);
		this.parameter_source = new RequestParameterSource(request, this.getParameters());
	}	
	public ParameterSource getParameterSource(){
		return this.parameter_source;
	}
	
	public ServiceDispatch getServiceDispatch(){
		String dispatch = (String)this.getServiceParameter(KEY_DEFAULT_DISPATCH);
		if( dispatch == null) return null;
		int dispatch_style = ServiceDispatch.getDispatchStyle( (String)getServiceParameter(KEY_DISPATCH_STYLE) );
		if( dispatch_style<0 ) dispatch_style = ServiceDispatch.DISPATCH_STYLE_REDIRECT;		
		int target_type = ServiceDispatch.getTargetType( (String)getServiceParameter(KEY_TARGET_TYPE));
		if( target_type<0) target_type = ServiceDispatch.TARGET_TYPE_URL;
		return application.createDispatch(this,target_type,dispatch,dispatch_style);
	}
	
	public String getLocalizedString( String key ){
		return application.getLocalizedString(getSessionLocale(), key);
	}

/* ------------------ End Access service configuration -------------------------------- */



/* ------------------ BEGIN Access service context -------------------------------- */
	
	static CompositeMap getContextChild( CompositeMap root_context,String name){
		CompositeMap child = root_context.getChild(name);
		return child == null? root_context.createChild(null,null,name): child;
	}
	
	/** get "model" section in service context */
	public CompositeMap getModel(){
		return getContextChild(getServiceContext(),KEY_MODEL);
	}

	/** get "view" section in service context */	
	public CompositeMap getView(){
		return getContextChild(getServiceContext(),KEY_VIEW);
	}	

	/** get "session" section in service context */	
	public CompositeMap getSession(){
		return getContextChild(getServiceContext(),KEY_SESSION);		
	}
	
	public static CompositeMap getSession(CompositeMap context){
		return getContextChild(context,KEY_SESSION);
	}

	/** get "parameter" section in service context */	
	public CompositeMap getParameters(){
		return getContextChild(getServiceContext(),KEY_PARAMETER);		
	}
	
	/** get a localized message for an ValidationException */
	public String getValidationMessage( ValidationException ve){
		return super.application.getLocalizedString(getSessionLocale(),ve.getErrorType());
	}
	
	/** get service that calls this service by 'include'  */
	public BaseService getCallingService(){
		return (BaseService)getServiceContext().get(KEY_CALLING_SERVICE_NAME);
	}

	/** set service that calls this service by 'include'  */
	public void setCallingService(BaseService svc){
		getServiceContext().put(KEY_CALLING_SERVICE_NAME, svc);
	}
	

/* ------------------ END Access service context -------------------------------- */


/* ------------- BEGIN default overridable behavior --------------------------------*/


	/** determine if this service creates any output */	
	public void setViewOutput( boolean b){
		_view_output = b;
	}
	
	public boolean hasViewOutput(){
		return _view_output;
	}

	/** get content type of this service */	
	public String getContentType(){
		Object ct = getServiceConfig().get("content-type");
		return ct==null?"text/html;charset=utf-8":ct.toString();
	}
	
	/** sets Locale for current session */
	void setSessionLocale(Locale l){
		getSession().put(KEY_SESSION_LOCALE, l);
	}
	
	/** get Locale for current session */
	public Locale getSessionLocale(){
	    Locale l = null;
	    l = (Locale)getSession().get(KEY_SESSION_LOCALE);
	    if(l==null){
    	    String locale_code = getSession().getString("locale_id");
    	    if(locale_code!=null)
    	        l = application.getResourceBundleFactory().getLocale(locale_code);
	    }
		if( l==null) 
		    l = request.getLocale();
		return l;		
	}
	
	public void parseParameter( CompositeMap param, CompositeMap target ) throws ServletException{		
		
		// Then parse parameters defined in service config
//		CompositeMap param = getParameterConfig();
		if( param != null)
			if( param.getChilds() != null){
				Collection errors = ParameterParser.parseParameter(this.getParameterSource(),param.getChilds(),target);
				if( errors != null) throw new ServletException("Invalid invocation for " + this.getServiceName() + ". the following error happened when parse parameter:" + errors.toString());
			}
		
	}
	
	/** parse parameter for service. By default convention, parameters
	 *  are parsed as java objects and put into "parameter" section of service context
	 */
	public void parseParameter()throws IOException, ServletException{

		CompositeMap param_map = getParameters();

		// First, parse pagesize and pagenum parameter
		ParameterParser.parseParameter(getParameterSource(),BaseService.page_parameters,param_map);
		
		parseParameter( getParameterConfig(), getParameters());

	}
	
	/**<code>
	 * <access-check>
	 *   <query Sql="statement"  test-field="" test-value="" />
	 * </access-check>
	 * </code>
	 * Called by framework when check user privilege.
	 * @throws IOException
	 * @throws NoPrivilegeException
	 */
	public void checkPrivilege() throws IOException, NoPrivilegeException {
		
		if( getCallingService() != null) return;
		
		CompositeMap access_check = getServiceConfigSection(KEY_ACCESS_CHECK);
		if( access_check == null) return;
		Iterator it = access_check.getChildIterator();
		if( it == null) return;
		
		CompositeMap model = getModel();
		try{
			databaseAccess( access_check, getParameters(), model);
		} catch(ServletException ex){
			throw new NoPrivilegeException(ex);
		}
		
		while( it.hasNext()){
			CompositeMap def = (CompositeMap)it.next();
			String test_value = def.getString(KEY_ACCESS_TESTVALUE);
			if( test_value == null) return;		
			Object result_value = model.getObject(def.getString(KEY_ACCESS_TESTFIELD));
			if( result_value == null) throw new NoPrivilegeException();
			if( !test_value.equals( result_value.toString()) )throw new NoPrivilegeException();
		}
		
	}	

	/** called by framework to perform some work before service happens */
	public void preService()
				  throws IOException, ServletException{
		// if a <pre-service> section is defined, execute database access content in this section
        CompositeMap pre_service_config = getServiceConfigSection(KEY_PRE_SERVICE);
        if(pre_service_config!=null) 
            databaseAccess(pre_service_config, getParameters(), getModel());
	}
	
	/** perform database access 
	 * @param access_def a CompositeMap whose childs contain access config
	 * @param params CompositeMap for input parameter
	 * @param target CompositeMap to hold return values
	 */
	public void databaseAccess(CompositeMap access_def, CompositeMap params, CompositeMap target) throws ServletException{
		
		long tick = System.currentTimeMillis();

		Connection conn = null;
		if( access_def == null) throw new IllegalArgumentException("database access config is null");
		Collection childs = access_def.getChilds();
		if( childs != null)		
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			DatabaseAccess.execute(this, childs,conn,params,target);
			conn.commit();			
		} catch(SQLException ex){
			if( conn != null)
			try{
			   if( !conn.isClosed())
			      conn.rollback();
			} catch(SQLException new_ex){				
			}
			throw new ServletException(ex);
		}
		finally{
			if( conn != null) 
			try{
				conn.close();
			}catch(Exception ex){
			}
		}
	}

	
	public void databaseAccess(String file_name, CompositeMap params, CompositeMap target) throws ServletException{
		try{
			CompositeMap access_def = application.getCompositeLoader().load(file_name);
			if(access_def==null) throw new IllegalArgumentException("can't load file "+file_name);
			databaseAccess(access_def, params, target);
		} catch( IOException ex){
			throw new ServletException(ex);
		} catch( org.xml.sax.SAXException sex){
			throw new ServletException(sex);			
		}
	}
	
	
	/** Called by framework to create Model if neccessary 
	 *  By default convention, the model is created as CompositeMap as put in service context
	 *  with name "model"
	 */
	public void createModel() throws  IOException,ServletException{
	    CompositeMap c = getModelConfig();
		if(c!=null)databaseAccess( c, getParameters(), getModel());		
	}
    
    protected boolean isHtmlFileName( String name ){
        return name.endsWith(".htm") || name.endsWith(".html");
    }
	
	/** add page template for service */
	protected void createTemplate(){
		CompositeMap view = getViewConfig();
		CompositeMap svc_context = getServiceContext();

		if( getCallingService() == null && view.getBoolean(KEY_TEMPLATE,true)){
		
			String page_tmpl = getServiceConfig().getString(KEY_PAGE_TEMPLATE, getApplicationConfig().getString(KEY_DEFAULT_PAGE_TEMPLATE) );
	
			if( page_tmpl != null)
            {
                if(isHtmlFileName(page_tmpl)){
                   view.setName("layout");
                   view.setNameSpace("std", ClassViewFactory.DEFAULT_NAMESPACE_URL);
                   view.put(Layout.KEY_TEMPLATE, page_tmpl);
                }else{
    				view.setNameSpace("jsp",JspViewFactory.NAMESPACE_URL);
    				view.setName(page_tmpl);
                }
				svc_context.createChild(null,null,KEY_VIEW).addChild(view);
                
			}
			else
				svc_context.addChild(view);
			
		}
		else
			svc_context.addChild(view);
	
	}

	/** Called by framework to create View if neccessary 
	 *  By default convention, the view is created as CompositeMap as put in service context
	 *  with name "view".
	 *  The default implementation gets view config from service config.
	 */
	public void createView() throws IOException,ServletException
	{
		CompositeMap view = getViewConfig();
		if( view == null) {
			this.setViewOutput(false);
			return;
		}
		
		if( view.getChilds() == null){
			this.setViewOutput(false);
			return;
		}

		// create template here
        createTemplate();
        setViewOutput(true);        

    }	
	
	/**
	 *  populate view after creation of model. 
	 */
	public void populateView() throws IOException,ServletException {
        CompositeMap view = getView();
		if( view == null) {
            return;
        }
 		view.iterate(new ViewPopulateIterator(),true);
	}
	

	/** Called by framework to perform actual service.
	 *  Non-output services may override this method to do service work.
	 *  The default implementation performs action
	 */
	public void doService() throws  IOException,ServletException{

	   if( "true".equals(this.getServiceParameter("dump"))){
	   		dumpContext();
	   		return;		   		
	   }
		
		CompositeMap action_conf = getActionConfig();
		if(action_conf != null){
			databaseAccess( action_conf, getParameters(), getModel());
		}
	}
	
	
	/**
	 * Iterates through each elements in view, set prompt to localized string
	 * for those whose prompt contains '.'
	 */
	public void localizeView(){		

		boolean ml = application.getApplicationConfig().getBoolean(KEY_MULTI_LANGUAGE, true);
		if( ml == false) return;
		
		IterationHandle it = new IterationHandle(){
			
			public int process( CompositeMap map){
				Object obj = map.get( org.lwap.ui.UIAttribute.ATTRIB_PROMPT);
				if( obj != null && obj instanceof String){
					String prompt = (String)obj;
					if( prompt.indexOf('.')>=0){
						map.put( org.lwap.ui.UIAttribute.ATTRIB_PROMPT, 
								 application.getLocalizedString( getSessionLocale(),prompt) );
					}
				}
				return IterationHandle.IT_CONTINUE; 
			}
		};
		
		getView().iterate(it,true);
		
	}
	
	public void preBuildView()  throws  IOException,ServletException{
		localizeView();		
	}
	
	public void initService(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response){
		setHttpObject(request,response);
		setServlet(servlet);
		if( "false".equals(getServiceParameter(KEY_SERVICE_OUTPUT)))
		 this.setViewOutput(false);		
	}
	
	
	public void handleException( Throwable thr) {
		application.handleException(this,thr);
	}
	
	/* ------------- END default overridable behavior -------------------- */	
	
	protected boolean isDump(){
		return  "true".equals(this.getServiceParameter("dump"));
	}
	
	protected void dumpContext() throws IOException{
		if( dumped) return;
		dumped = true;
   		response.setContentType("text/xml;charset=utf-8");
   		//response.setLocale(Locale.CHINESE);
   		//response.setLocale(Locale.)
   		PrintWriter out = response.getWriter();
   		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
   		out.println(getServiceContext().toXML());
   		out.flush();
	}

	public void buildOutputContent() throws IOException,org.lwap.mvc.ViewCreationException, ServletException {
        
        CompositeMap view = getServiceContext().getChild(KEY_VIEW);
		   if( view == null) return;
		   if(isDump()){
		   		dumpContext();
		   		return;		   		
		   }
		   preBuildView();
		   response.setContentType(getContentType());
		   
		   // should be response.setLocale(getSessionLocale()), to be enhanced later
		   // response.setLocale(getSessionLocale());
		   // response.setLocale(Locale.CHINESE);
		   
		   // set content expire
		   boolean cache = this.getServiceConfig().getBoolean(KEY_CACHE, false);
		   if( !cache){
			  response.setHeader("pragma", "no-cache");
			  response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
		   }

		   PageContext page_context = null;
           boolean upc = application.getApplicationConfig().getBoolean("use-page-context", false);
           if(upc){
               page_context = JspFactory.getDefaultFactory().getPageContext(servlet, request, response, null, false, JspWriter.DEFAULT_BUFFER, true);
           }
           
           ServletBuildSession _session = new ServletBuildSession( 
                getViewBuilderStore(), 
				getServletContext(), 
                request, 
                response, 
                page_context);
           //_session.setStringProvider(this);
           _session.setService(this);
           if(upc){
               _session.setAutoFlush(true);
           }
           
           try{
           		_session.applyViews( getModel(), getView().getChilds() );
           } finally {
           		_session.endSession();
           }
 		
	}
	
	
		

/* ------------- END default overridable behavior --------------------------------*/

	/**
	 *  Default service framework calls the following methods sequentially:
	 *  
	 *  <code>parseParameter()</code>
	 *  <code>preService()</code>
	 *  <code>checkPrivilege()</code>
	 *  <code>createModel()</code>
	 *  <code>createView()</code>
	 *  <code>populateView()</code>
	 *  <code>doService()</code>
	 *  
	 *  In the end, if the services generates some output, an internal method
	 *  will be called to build output by "mode" and "view" stored in service context.
	 *  If the service doesn't creat any output, <code></code> will be called to
	 *  perform inter-service jump.
	 */
	public void service(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response )
				  throws Exception{
	
		initService(servlet, request,response);
		parseParameter();
		preService();
		checkPrivilege();
		createModel();
		createView();
		populateView();		
		doService();
		
		if( hasViewOutput()){
			try{
				buildOutputContent();
			} catch( org.lwap.mvc.ViewCreationException ex){
				//ex.printStackTrace();
				throw new ServletException(ex);
			}
		} else{
			ServiceDispatch disp = getServiceDispatch();
			if( disp != null) disp.dispatch();
		}
		
	}	
}
