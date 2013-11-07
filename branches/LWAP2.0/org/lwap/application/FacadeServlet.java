/**
 * Created on: 2002-11-10 15:57:18
 * Author:     zhoufan
 */
package org.lwap.application;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.xml.sax.SAXException;

import uncertain.core.UncertainEngine;


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
  	service_name = service_name.substring(service_name.indexOf('/')+1);
  	return service_name ;
//  	return servlet_context.getRealPath(request.getServletPath());
  }
 
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
  	 }catch(Throwable thr){
         throw new ServletException(thr);        
     }finally {
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
