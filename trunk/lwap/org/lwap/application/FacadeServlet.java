/**
 * Created on: 2002-11-10 15:57:18
 * Author:     zhoufan
 */
package org.lwap.application;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.lwap.database.TransactionFactory;
import org.lwap.mvc.BuiltInViewFactory;
import org.lwap.mvc.ClassViewFactory;
import org.lwap.mvc.DefaultViewFactory;
import org.lwap.mvc.ViewConfig;
import org.lwap.mvc.ViewFactoryStore;
import org.lwap.mvc.servlet.JspViewFactory;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.ObjectSpace;
import uncertain.util.LoggingUtil;


//import com.handchina.hrms.util.*;


public class FacadeServlet extends HttpServlet {

  public static final String	DEFAULT_CONFIG_PATH = "/WEB-INF/uncertain.xml";
  public static final String	KEY_CONFIG_PATH = "config-path";	
    
  public static final String KEY_APPLICATION_PATH = "application-path";
  public static final String KEY_CONFIG_FILE =   "config-file";
  public static final String KEY_APPLICATION = "application-object";

  public static final String KEY_VIEW_BUILDER_STORE = "view-builder-store";    
  public static final String KEY_TEMPLATE_PATH = "template-path";
  
  String 			        app_path;
  String					config_file;
  String					prompt_file;
  WebApplication			application;
  ServletContext            servlet_context;
  ServletConfig				config;
  
  UncertainEngine			uncertainEngine;  

  public WebApplication getApplication(){
      return application;
  }
  
  
  String get_service_name( HttpServletRequest request){
  	String service_name = request.getServletPath();
  	service_name = service_name.substring(service_name.lastIndexOf('/')+1);
  	return service_name ;
//  	return servlet_context.getRealPath(request.getServletPath());
  }
  
  
/*  
  
  void create_view_builder_store(WebApplication app){
  		CompositeMap app_config = app.getApplicationConfig();
        String template_context = app_config.getString(KEY_TEMPLATE_PATH);
        ServletContext context = servlet_context.getContext(template_context);
		if(context == null) context = servlet_context;


        ViewFactoryStore builder_store = new ViewFactoryStore();
        
        File path = new File(context.getRealPath("/"));
        try{
        	ViewConfig   view_config = new ViewConfig(template_context, path);
        	builder_store.setViewConfig(view_config);
        } catch(FileNotFoundException ex){
        }
        
        JspViewFactory   	jsp_builder = new JspViewFactory(context, template_context);
        ClassViewFactory 	builtin = BuiltInViewFactory.createBuiltInViewBuilder();
        DefaultViewFactory	default_builder = new DefaultViewFactory();
        
        builder_store.registerViewFactory( jsp_builder );
        builder_store.registerViewFactory( builtin);        
        builder_store.registerViewFactory( default_builder);
        builder_store.setDefaultFactory(default_builder);
        
        app.setViewBuilderStore(builder_store);
//        app_config.put(KEY_VIEW_BUILDER_STORE,builder_store);
  }
  
  public void initUncertain(ServletConfig config) throws ServletException{
      
      ServletContext servletContext = config.getServletContext();
      String config_path = config.getInitParameter(KEY_CONFIG_PATH);
      if(config_path==null) config_path = DEFAULT_CONFIG_PATH;

      String config_dir =servletContext.getRealPath("/WEB-INF");
      String config_file="uncertain.xml";
      String pattern = config.getInitParameter("config-pattern");
      if(pattern==null) pattern = ".*\\.config";

      try{
	        uncertainEngine = new UncertainEngine(new File(config_dir), config_file);
	        ObjectSpace os = uncertainEngine.getObjectSpace();
	        os.registerParameter(ServletConfig.class,config);
	        os.registerParameter(ServletContext.class,servletContext);
	        os.registerParameter(HttpServlet.class, this);
            os.registerParameter(application);
            CompositeLoader loader = uncertainEngine.getCompositeLoader();
            CompositeMap default_config = loader.loadFromClassPath("org.lwap.application.DefaultClassRegistry");
            ClassRegistry reg = (ClassRegistry)uncertainEngine.getOcManager().createObject(default_config);
            uncertainEngine.addClassRegistry(reg, false);
            //uncertainEngine.getOcManager().populateObject(default_config, uncertainEngine);
            if(application.data_source!=null){
                os.registerParameter(DataSource.class, application.data_source);
                os.registerParamOnce(TransactionFactory.class, application.transaction_factory);
            }            
	        LoggingUtil.setHandleLevels(uncertainEngine.getLogger().getParent(), Level.INFO);
	        //uncertainEngine.getLogger().setLevel(Level.INFO);
	        //uncertainEngine.getCompositeLoader().setCaseInsensitive(true);
	        
	        uncertainEngine.scanConfigFiles(pattern);
	        
      }catch(Exception ex){
          throw new ServletException(ex);
      }
      
  }  
  


  public void init(ServletConfig config) throws ServletException{
  	this.config = config;
  	servlet_context = config.getServletContext();
  	app_path = config.getInitParameter(KEY_APPLICATION_PATH);
  	if(app_path == null) app_path = servlet_context.getRealPath("");
  	config_file = config.getInitParameter(KEY_CONFIG_FILE);
 
    try{
  		application = new WebApplication(app_path,config_file,servlet_context);
// -------------- set case insensitive -------------------------------------------------  		
  		//application.getCompositeLoader().setCaseInsensitive(true);
    	CompositeMap application_conf =  application.getApplicationConfig();
    	create_view_builder_store(application);
  	} catch(ApplicationInitializeException ae){
  		Throwable thr = ae.getCause();
  		if( thr != null)
  			throw new ServletException(thr);
  		else
  			throw new ServletException(ae);	
  	}
  	servlet_context.setAttribute(KEY_APPLICATION, application);
  	
  	initUncertain(config);
  	application.setUncertainEngine(uncertainEngine);
  	if( application.getResourceBundleFactory() == null){
  			DefaultResourceBundleFactory fact = new DefaultResourceBundleFactory("prompt");
  			try{
  				fact.getResourceBundle(Locale.getDefault());
  				application.setResourceBundleFactory(fact);
  			} catch(Exception ex){
  				System.out.println("[init] "+ex.getMessage());
  			}
  	}

    servlet_context.setAttribute("application", application);
    servlet_context.setAttribute("uncertain", uncertainEngine);

  }
  
  */
  
  public void init(ServletConfig config) throws ServletException{
        this.config = config;
        servlet_context = config.getServletContext();
        application = WebContextInit.getApplication(servlet_context);
        if(application==null){
            WebContextInit wi = new WebContextInit();
            try{
                wi.init(servlet_context);
            }catch(Exception ex){
                throw new ServletException(ex.getCause()==null?ex:ex.getCause());
            }
            application = wi.getApplication();
        }
        uncertainEngine = application.getUncertainEngine();
  }
  
  public void service(HttpServletRequest request, HttpServletResponse response)
  throws ServletException, IOException {
	 request.setCharacterEncoding("utf-8");
	 Service service;
	 try{
  	 	service = application.getService(get_service_name(request));
	 } catch(ServiceInstantiationException ex){
	    //ex.printStackTrace(); 
	 	Throwable thr = ex.getCause();
	 	if( thr == null) 
	 		response.sendError(500, ex.getMessage());
	 	else if( thr instanceof IOException )
	 		response.sendError(404, "service not found");
	 	else if( thr instanceof SAXException )
	 		response.sendError(500, "error when parse service file:"+thr.getMessage());	
	 	else{
	 		//thr.printStackTrace();
	 	    response.sendError(500, "Exception:"+thr.getClass().getName()+":"+thr.getMessage());
	 	}
	 	return;
	 }
	 
  	 int ret = application.initService(request,response,service);
     if( ret == ServiceParticipant.BREAK_WHOLE_SERVICE )
         return;
  	 try{ 	 
  	 	service.service(this, request,response );
  	 } finally {
  	 	service.finish();
  	 }
  	 
  }

  public void destroy(){
      //super.destroy();
  	  application.shutdown();
  }
  
  public ServletConfig getServletConfig(){
  	  return this.config;
  }
  
}
