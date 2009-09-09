/*
 * Created on 2005-7-27
 */
package org.lwap.application;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;
import uncertain.event.Configuration;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;

/**
 * LwapServlet
 * @author Zhou Fan
 * 
 */
public class LwapServlet  extends HttpServlet {
    
    public static final String	DEFAULT_CONFIG_PATH = "/WEB-INF/uncertain.xml";
    public static final String	KEY_CONFIG_PATH = "config-path";	
    
    UncertainEngine		uncertainEngine;
    ServletConfig		servletConfig;
    ServletContext		servletContext;

    public void init(ServletConfig config) throws ServletException{
        servletConfig = config;
        servletContext = config.getServletContext();
        String config_path = servletConfig.getInitParameter(KEY_CONFIG_PATH);
        if(config_path==null) config_path = DEFAULT_CONFIG_PATH;

        String config_dir =servletContext.getRealPath("/WEB-INF");
        String config_file="uncertain.xml";
        String pattern = config.getInitParameter("config-pattern");
/*        
        InputStream config_stream = servletContext.getResourceAsStream(config_path);
        if(config_stream==null) throw new ServletException("Can't load config file "+config_path);
 */
        try{
	        uncertainEngine = new UncertainEngine(new File(config_dir), config_file);
	        IObjectRegistry os = uncertainEngine.getObjectRegistry();
	        os.registerInstance(ServletConfig.class,servletConfig);
	        os.registerInstance(ServletContext.class,servletContext);
	        os.registerInstance(HttpServlet.class, this);
	        uncertainEngine.scanConfigFiles(pattern);
        }catch(Exception ex){
            throw new ServletException(ex);
        }
        
    }

    String get_service_name( HttpServletRequest request){
      	String service_name = request.getServletPath();
      	service_name = service_name.substring(service_name.lastIndexOf('/')+1);
      	return service_name ;
      }
    
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String service_name = get_service_name(request);
        CompositeMap config = null;
        String proc_name = null;
        try{
            config = uncertainEngine.getCompositeLoader().load(service_name);
            proc_name = config.getString("type");
        }catch(Exception ex){
            throw new ServletException(ex);
        }
        if(proc_name==null) throw new ServletException("configuration error, 'type' not set in "+service_name);
        Procedure proc = uncertainEngine.loadProcedure(proc_name);
        if(proc==null) throw new ServletException("Can't load procedure "+proc);
        Configuration pc = uncertainEngine.createConfig();
        pc.loadConfig(config);
        ProcedureRunner runner = uncertainEngine.createProcedureRunner(proc);
        runner.addConfiguration(pc);
        //System.out.println("service participant list:"+pc.getParticipantList());
        
        CompositeMap context = runner.getContext();
        context.put("request", request);
        context.put("response", response);
        
        runner.run();
    }

}
