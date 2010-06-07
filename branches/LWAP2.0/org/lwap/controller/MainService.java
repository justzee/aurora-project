/*
 * Created on 2005-10-8
 */
package org.lwap.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.lwap.application.BaseService;
import org.lwap.application.NoPrivilegeException;
import org.lwap.application.ResourceBundleFactory;
import org.lwap.application.ServiceDispatch;
import org.lwap.application.event.IServiceListenerManager;
import org.lwap.application.event.SessionController;
import org.lwap.database.ConnectionRollback;
import org.lwap.database.DBUtil;
import org.lwap.database.DatabaseAccess;
import org.lwap.database.DatabaseEntry;
import org.lwap.database.PerformanceRecorder;
import org.lwap.feature.ExceptionProcessor;
import org.lwap.mvc.ViewCreationException;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.RuntimeContext;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.LoggingContext;
import uncertain.proc.IEntry;
import uncertain.proc.IExceptionHandle;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;

/**
 * UncertainService
 * @author Zhou Fan
 * 
 */
public class MainService extends BaseService 
implements Configuration.IParticipantListener
{
    
    public static final String KEY_FINAL_PAGE = "final-page";
    public static final String KEY_PROCEDURE = "procedure";    
    public static final String KEY_VIEW_OUTPUT = "output";
    public static final String KEY_NEXT_PROCEDURE = "NextProcedure"; 
    //public static final String KEY_DISPATCH = "Dispatch";
/*    
    public static final String KEY_SERVICE_STATE = "ServiceState";
    public static final String KEY_SERVICE_OBJECT = "ServiceObject";
 */   
    public static final String KEY_REQUEST = "httprequest";
    public static final String KEY_RESPONSE = "httpresponse";
    public static final String KEY_UNCERTAIN_ENGINE = "theuncertainengine";
    public static final String KEY_SERVICE_INSTANCE = "serviceinstance";
    public static final String KEY_CURRENT_CONNECTION = "_instance.java.sql.Connection";
    public static final String KEY_RESOURCE_BUNDLE = "_instance.java.util.ResourceBundle";    
    public static final String KEY_CONNECTION_SET  = "_collection.java.sql.connection";
    public static final String KEY_IMPORT_SUCCESS = "ImportSuccess";
    
    static final String LINE_SEPARATOR = System.getProperty("line.separator");    
    
    static HashMap	proc_map = new HashMap();
    
    UncertainEngine		uncertainEngine;
    ProcedureRunner		runner;
    LinkedList			controllerList = new LinkedList();
    Configuration       configuration;
    ILogger             mLogger;
    
    public static MainService getServiceInstance(CompositeMap context){
        return (MainService)context.get(KEY_SERVICE_INSTANCE);
    }
    
/*
    public static void setDispatch( CompositeMap context, String disp){
        context.put(KEY_DISPATCH, disp);
    }
    
    public static String getDispatch( CompositeMap context ){
        return context.getString(KEY_DISPATCH);
    }
*/
    /**
     * @see org.lwap.application.BaseService#buildOutputContent()
     */
    public void onBuildOutputContent() throws IOException, ViewCreationException,
            ServletException {
        //response.getWriter().println("test output");
        super.buildOutputContent();
    }
    /**
     * @see org.lwap.application.BaseService#checkPrivilege()
     */
    public void onCheckPrivilege() throws IOException, NoPrivilegeException {
        super.checkPrivilege();
    }
    /**
     * @see org.lwap.application.BaseService#createModel()
     */
    public void onCreateModel() throws IOException, ServletException {
        super.createModel();
    }

    /**
     * @see org.lwap.application.BaseService#createView()
     */
    public void onCreateView() throws IOException, ServletException {
        super.createView();        
    }
    
    public void onPopulateView() throws IOException, ServletException {
        super.populateView();
    }

    /**
     * @see org.lwap.application.BaseService#parseParameter()
     */
    public void onParseParameter() throws IOException, ServletException {
        super.parseParameter();
    }
    
    /**
     * @see org.lwap.application.BaseService#preService()
     */
    
    public void onPrepareService() throws IOException, ServletException {
        super.preService();
    }
    
    public void onDoAction()throws IOException, ServletException {
        CompositeMap action_conf = getActionConfig();
        String action_name = getActionName();
        if(action_conf==null && action_name!=null){
	        application.getLogger().warning(getServiceName()+":service action "+ action_name + " is not found in service config");
	    }else{
	        super.doService();
            //databaseAccess( action_conf, getParameters(), getModel());
        }
    }
  
    
    /**
     * @see org.lwap.application.BaseService#getServiceDispatch()
     */
    public ServiceDispatch getServiceDispatch() {
        ServiceDispatch disp = null;
        String dispatch = service_context.getString(SessionController.KEY_DISPATCH_URL);
        if(dispatch!=null)
            disp = application.createDispatch(
    				this,
    				ServiceDispatch.TARGET_TYPE_URL,
    				dispatch,
    				ServiceDispatch.DISPATCH_STYLE_REDIRECT
    			);
        if(disp!=null)
            return disp;
        else
            return super.getServiceDispatch();
    }
    public void onDoDispatch()throws IOException, ServletException {
        ServiceDispatch disp = getServiceDispatch();
		if( disp != null) disp.dispatch();
    }
    
    public boolean addParticipant(Object pInst){
        if(pInst instanceof IController) 
            controllerList.add(pInst);
        return true;
    }
    
    protected void doinit(HttpServlet servlet, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        uncertainEngine = application.getUncertainEngine();
		initService(servlet, request,response);
		service_context.put(KEY_UNCERTAIN_ENGINE, uncertainEngine);
		service_context.put(KEY_REQUEST, request);
		service_context.put(KEY_RESPONSE, response);
		service_context.put(KEY_SERVICE_INSTANCE, this);    
        uncertainEngine.initContext(service_context);        
    }
/*    
    protected void checkException(ProcedureRunner r) throws ServletException {
		if(r.getException()!=null){
		    throw new ServletException(r.getException());
		}        
    }
  */ 
    // cached procedure loading
    protected Procedure loadProcedure(String proc_name){
        Procedure proc = (Procedure)proc_map.get(proc_name);
        if(proc==null){
            proc = uncertainEngine.loadProcedure(proc_name);
            proc_map.put(proc_name, proc);
        }
        return proc;
    }
    
    protected boolean runProcedure(ProcedureRunner r, String proc_name) throws Throwable {
        //Procedure proc = uncertainEngine.loadProcedure(proc_name);
        Procedure proc = loadProcedure(proc_name);
        if(proc==null)  throw new ServletException("Can't load procedure "+proc_name);
        r.setProcedure(proc);
        r.run();
        Throwable thr = r.getException();
		if( thr != null ){
            boolean is_self_handle = service_properties.getBoolean("exception-handle", false);
            if( !is_self_handle){
            ExceptionProcessor processor = 
                (ExceptionProcessor)uncertainEngine.getObjectRegistry().getInstanceOfType(ExceptionProcessor.class);
                if(processor!=null){
                     try{
                         ExceptionProcessor.Processor ep = processor.getProcessor(thr.getClass().getName());
                         if(ep!=null){
                             // invoke handle
                             Class handleClass = ep.getHandleClass();
                             if(handleClass!=null){
                                 IExceptionHandle handle = (IExceptionHandle)handleClass.newInstance();
                                 handle.handleException(r, thr);
                             }                             
                             // dispatch
                             String url = ep.getDispatchURL();                             
                             if(url!=null){
                                 url = TextParser.parse(url, r.getContext());
                                 this.getResponse().sendRedirect(url);
                             }else
                                 //mLogger.info("No direct url defined for exception " + thr.getClass().getName());
                             return false;
                         }else{
                             //mLogger.info("Can't find exception handle for class " + thr.getClass().getName() );
                         }
                    }catch(Exception ex){
                        mLogger.log(Level.SEVERE, "Error when processing exception", ex);
                    }
                }
            }    
		    throw r.getException();
		}
        return true;
    }
    
    // Parse pre-defined parameters
    public void parseBuiltinParameters(){
        HttpServletRequest request = getRequest();
        CompositeMap params = getParameters();
        for(int i=0; i<StateFlag.PRE_PARSED_PARAMETERS.length; i++){
            String param_name = StateFlag.PRE_PARSED_PARAMETERS[i];
            String value = request.getParameter(param_name);
            if(value!=null)
                params.put(param_name, value);
        }
    }
    
    public void setNextProcedure(String proc_name){
        service_context.put(KEY_NEXT_PROCEDURE, proc_name);
    }
    
    public Connection getConnection(String name) throws SQLException {
        return getDataSource().getConnection();
    }
    
    public static Connection getConnection(CompositeMap context){
        return (Connection)context.get(KEY_CURRENT_CONNECTION);
    }
   
    /**
     * Change implementation to use uncertain procedure engine
     */
    public void databaseAccess(CompositeMap access_def, CompositeMap params, CompositeMap target) 
        throws ServletException 
    {
        boolean dump = isTraceOn();
        if(runner==null){
            super.databaseAccess(access_def,params,target);
            return;
        }
        if( access_def == null) throw new IllegalArgumentException("database access config is null");
        Iterator it = access_def.getChildIterator();
        Connection conn = null;
        Procedure proc = null;
        
        if( it != null)
        try
        {
            
            conn = getConnection();
            conn.setAutoCommit(false);
            //runner.fireEvent("ConnectionCreate", new Object[]{conn} );
            CompositeMap map = getServiceContext();
            map.put(KEY_CURRENT_CONNECTION, conn);            
            proc = new Procedure(uncertainEngine.getOcManager());
            boolean rp = application.isRecordRerformance();
            PerformanceRecorder recorder = application.getPerformanceRecorder();
            while(it.hasNext()){
                CompositeMap item = (CompositeMap)it.next();                
                if(dump){
                    mLogger.config("[DatabaseAccess] running "+item.toXML());
                }

                DatabaseAccess da = DatabaseAccess.getInstance(item);
                if(da!=null){
                    if(dump){
                        mLogger.config("[DatabaseAccess] to run as "+da.getClass().getName());
                    }

                    if(rp){ 
                        da.setOwner(getServiceName());
                        da.setPerformanceRecorder(recorder);
                    }
                    Connection real_conn = conn;
                    String data_source = da.getDataSource();
                    if(data_source!=null)
                        real_conn = getNamedConnection(data_source);
                    DatabaseEntry entry = new DatabaseEntry(da,real_conn,params,target,this);
                    proc.addEntry(entry);
                }else{
                    Object inst = super.application.getUncertainEngine().getOcManager().createObject(item); 
                    if(inst==null)
                        inst = configuration.getInstance(item);
                    if(inst==null)
                        inst = configuration.getFeatureInstance(item, IEntry.class);
                    if(inst==null) {
                        mLogger.warning("Can't get participant instance from config:"+item.toXML()+" config hash:" + item.hashCode());
                        continue;
                    }
                    else{                        
                        if(inst instanceof IEntry) {
                            if(dump){
                                mLogger.info("[DatabaseAccess] Adding entry"+inst.getClass().getName());
                            }
                            proc.addEntry((IEntry)inst);
                        }
                        else 
                            mLogger.warning("Unknown entry:"+item.getName());
                    }
                }
            }
            runner.call(proc);
            if(runner.getLatestException()==null){
                //mLogger.info("commited trasaction");
                conn.commit();
            }
        }catch(Throwable ex){
            if(conn!=null){
                try{
                    conn.rollback();
                }catch(SQLException sex){
                    uncertainEngine.logException("Error when closing connection", sex);
                }
            }
            DBUtil.closeConnection(conn);
            throw new ServletException(ex);
        }finally{
            //runner.fireEvent("ConnectionClose", new Object[]{conn} );
            DBUtil.closeConnection(conn);
            closeConnectionSet();
            getServiceContext().remove(KEY_CURRENT_CONNECTION);
            if(proc!=null)
                proc.clear();
            
        }

    }
    
    void createErrorDesc(){
        StringBuffer msg = new StringBuffer();
        msg.append("service name:").append(getServiceName()).append(LINE_SEPARATOR);
        msg.append("context dump:").append(LINE_SEPARATOR);
        msg.append(getServiceContext().toXML()).append(LINE_SEPARATOR);
        super.setErrorDescription(msg.toString());
    }
    
    public void cleanUp(){        
        if(service_context!=null){
            try{
                uncertainEngine.destroyContext(service_context);
            }catch(Throwable thr){
                System.out.println(thr.getMessage());
            }
            service_context.clear();
        }
        if(configuration!=null)
            configuration.clear();
        if(service_properties!=null)
            service_properties.clear();
        if(controllerList!=null)
            controllerList.clear();
        
    }
    
    public void prepare(){
        // parse builtin parameters
        parseBuiltinParameters();       
        configuration = uncertainEngine.createConfig();
        // Add application defined event handles
        IServiceListenerManager  slm = application.getServiceListenerManager();
        slm.populateConfiguration(configuration);
        // load configuration
        CompositeMap service_config = getServiceConfig();
        service_config.put(KEY_SERVICE_INSTANCE, this);
        configuration.addParticipant(this);
        configuration.loadConfig(service_config, this);
        
        // set resource bundle
        ResourceBundleFactory fact = application.getResourceBundleFactory();
        if(fact != null){
            ResourceBundle bundle = fact.getResourceBundle( this.getSessionLocale() );
            service_context.put(KEY_RESOURCE_BUNDLE, bundle);
        }
    }
    
    String getLoggingTopic(){
        return "org.lwap.service";      
    }
    
    public boolean isTraceOn(){
        Object is_trace = service_properties.get("trace");
        if(is_trace==null)
            is_trace = this.getApplicationConfig().get("trace");
        if( is_trace==null) 
            return false;
        else
            return "true".equalsIgnoreCase(is_trace.toString());
    }

    /**
     * @see org.lwap.application.Service#service(javax.servlet.http.HttpServlet, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void service(HttpServlet servlet, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        CompositeMap _context = getServiceContext();
        RuntimeContext rtc = RuntimeContext.getInstance(_context);        
        doinit(servlet,request,response);
        // Prepare logger
        ILoggerProvider provider = LoggingContext.getLoggerProvider(_context);
        mLogger = provider.getLogger(getLoggingTopic());
        mLogger.config("============== ### Enter Service:"+request.getRequestURL().toString()+" ### ===================================================");

        try{
            boolean trace = isTraceOn();         
            prepare();

    		rtc.setInstanceOfType(ILogger.class, mLogger);
    		
            
            /* ------------- Modified on 29 Nov 2008 ------------------------*/
            /* ------------- Do pre-service check steps ---------------------*/
            
            //create procedure runner & add servie config
            runner = uncertainEngine.createProcedureRunner();
            runner.setContext(_context);
            runner.setConfiguration(configuration);
            runner.addFirstExceptionHandle(new ConnectionRollback());
            ILogger proc_logger = provider.getLogger(ProcedureRunner.LOGGING_TOPIC);
            runner.setLogger(proc_logger);

            // get pre service proc name
            String pre_service_proc = ControllerProcedures.PRE_SERVICE;
            // do pre service check                        
            if(this.getCallingService()==null){
                if(!runProcedure(runner, pre_service_proc)){
                    mLogger.warning("error running "+pre_service_proc+" for "+this.getServiceName());                    
                    return;
                }
                SessionController state = SessionController.createSessionController(_context);
                if( !state.getContinueFlag() ){
                    String url = state.getDispatchUrl(); 
                    if( url!=null ){
                            response.sendRedirect(url);
                            return;
                    }else
                        return;
                }
            }
            
            /* ------------- End Modify -------------------------------------*/
       		mLogger.log(Level.CONFIG, "Participant list:"+configuration.getParticipantList().toString());

       		// get procedure name to run
    		String 			procedure_name = null;
    		if(controllerList.size()>0){
    		    Iterator it = controllerList.iterator();
    		    while(it.hasNext()){
    		        IController controller = (IController)it.next();
    		        controller.setServiceInstance(this);
    		        if(controller.detectAction(request,service_context)==IController.ACTION_DETECTED){
    		            procedure_name = controller.getProcedureName();
    		        }
    		    }
    		}
    		if(procedure_name==null) 
    		    procedure_name = getServiceConfig().getString(KEY_PROCEDURE,ControllerProcedures.BASE_SERVICE);
    
    		// run procedure
    		runner.fireEvent(ControllerProcedures.PopulateSerivceConfig);
    		do{
    		    service_context.remove(KEY_NEXT_PROCEDURE);
                if(trace){
                    mLogger.log(Level.CONFIG, "service procedure set to "+procedure_name);
                }
    			if(!runProcedure(runner, procedure_name))
                    return;
    			procedure_name = service_context.getString(KEY_NEXT_PROCEDURE);
    		}while(procedure_name != null);
    		// check for dump
    		if(super.isDump()){ 
    		    dumpContext();
    		    return;
    		}
    		// create response
    		if(_context.get(KEY_VIEW_OUTPUT)==null)
    		    _context.put(KEY_VIEW_OUTPUT, new Boolean(hasViewOutput()));
    
    		runProcedure(runner, ControllerProcedures.CREATE_RESPONSE);
        }catch(Exception ex){
            createErrorDesc();
            mLogger.log(Level.SEVERE, getErrorDescription(), ex);
            uncertainEngine.logException( getErrorDescription(), ex);
            throw ex;
        }catch(Throwable err){
            createErrorDesc();
            mLogger.log(Level.SEVERE, getErrorDescription(), err);
            uncertainEngine.logException( getErrorDescription(), err);
            throw new RuntimeException(err);
        }finally{
            cleanUp();
        }
    }
    
    public void onInvalidState() throws ServletException {
        mLogger.info(getServiceContext().toXML());
        throw new ServletException("Service internal error");
    }
    
    /* --------- Added on 21, June --------------------------------------*/
    /* --------- Operate on multiple database connection ----------------*/
    public Connection getNamedConnection( String name )
        throws SQLException
    {
        Map conn_set = (Map)service_context.get(KEY_CONNECTION_SET);
        if( conn_set==null){
            conn_set = new HashMap();
            service_context.put(KEY_CONNECTION_SET, conn_set);            
        }
        Connection conn = (Connection)conn_set.get(name);
        if(conn==null){
            DataSource ds = (DataSource) getApplicationConfig().get(name);
            if(ds==null)
                throw new IllegalArgumentException("Can't find named DataSource:"+name);
            conn = ds.getConnection();
            mLogger.config("Creating named connection "+name);
            conn_set.put(name, conn);
        }else{
            mLogger.config("Reusing named connection "+name);
        }
        return conn;
    }

    /** close all named connections created in databaseAccess */
    protected void closeConnectionSet(){
        Map conn_map = (Map)service_context.get(KEY_CONNECTION_SET);
        if(conn_map!=null){
            Collection c = conn_map.entrySet();
            if(c!=null){
                Iterator it = c.iterator();
                while(it.hasNext()){
                    Map.Entry entry = (Map.Entry)it.next();
                    Connection conn = (Connection)entry.getValue();
                    String name = (String)entry.getKey();
                    mLogger.config("Closing db connection with name"+name);
                    DBUtil.closeConnection(conn);                    
                }
            }
            conn_map.clear();
            getServiceContext().remove(KEY_CONNECTION_SET);
        }        
    }
    
    public Configuration getConfiguration(){
        return  configuration;
    }
    
    /*
    public void reloadConfig(){
        configuration.clear();
        prepare();
        //configuration.loadConfig(getServiceConfig());
    }
    */
    
    
}
