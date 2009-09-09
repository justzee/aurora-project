/*
 * Created on 2007-11-20
 */
package org.lwap.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.lwap.database.TransactionFactory;
import org.lwap.mvc.BuiltInViewFactory;
import org.lwap.mvc.ClassViewFactory;
import org.lwap.mvc.DefaultViewFactory;
import org.lwap.mvc.ViewConfig;
import org.lwap.mvc.ViewFactoryStore;
import org.lwap.mvc.servlet.JspViewFactory;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.DirectoryConfig;
import uncertain.core.UncertainEngine;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IObjectRegistry;
import uncertain.util.LoggingUtil;

public class WebContextInit implements ServletContextListener {   
    
    public static final String KEY_TEMPLATE_PATH = "template-path";    
    public static final String KEY_APPLICATION = "application-object";    
    public static final String KEY_LOG_PATH = "log-path";
    
    UncertainEngine     uncertainEngine;
    WebApplication      application;
    
    public WebContextInit(){
        
    }
    
    public static WebApplication getApplication(ServletContext context){
        return (WebApplication)context.getAttribute(KEY_APPLICATION);
    }
    
    
    public void initUncertain(ServletContext servletContext) 
        throws Exception
    {
        String config_path = "/WEB-INF/uncertain.xml";
        String config_dir =servletContext.getRealPath("/WEB-INF");
        String config_file="uncertain.xml";
        String pattern = ".*\\.config";

            uncertainEngine = new UncertainEngine(new File(config_dir), config_file);
            uncertainEngine.setName(servletContext.getServletContextName());
            DirectoryConfig dirConfig = uncertainEngine.getDirectoryConfig();
            dirConfig.setBaseDirectory(servletContext.getRealPath("/"));
            String log_path = servletContext.getInitParameter(KEY_LOG_PATH);
            if(log_path!=null){
                File file = new File(log_path);
                if(!file.exists()||!file.isDirectory())
                    throw new ConfigurationError("Invalid log path in web.xml:"+log_path);
                dirConfig.setLogDirectory(log_path);
            }
            
            //dirConfig.setLogDirectory(servletContext.log());
            IObjectRegistry os = uncertainEngine.getObjectRegistry();
            //os.registerParameter(ServletConfig.class,config);
            os.registerInstance(ServletContext.class,servletContext);
            os.registerInstance(HttpServlet.class, this);
              os.registerInstance(application);
              CompositeLoader loader = uncertainEngine.getCompositeLoader();
              CompositeMap default_config = loader.loadFromClassPath("org.lwap.application.DefaultClassRegistry");
              ClassRegistry reg = (ClassRegistry)uncertainEngine.getOcManager().createObject(default_config);
              uncertainEngine.addClassRegistry(reg, false);
              //uncertainEngine.getOcManager().populateObject(default_config, uncertainEngine);
              if(application.data_source!=null){
                  os.registerInstance(DataSource.class, application.data_source);
                  os.registerInstanceOnce(TransactionFactory.class, application.transaction_factory);
              }            
            //LoggingUtil.setHandleLevels(uncertainEngine.getLogger().getParent(), Level.INFO);
            //uncertainEngine.getLogger().setLevel(Level.INFO);
            //uncertainEngine.getCompositeLoader().setCaseInsensitive(true);
            
            uncertainEngine.scanConfigFiles(pattern);
        
    }  
    
    void create_view_builder_store(WebApplication app, ServletContext servlet_context){
        CompositeMap app_config = app.getApplicationConfig();
        String template_context = app_config.getString(KEY_TEMPLATE_PATH);
        ServletContext context = servlet_context.getContext(template_context);
        if(context == null){
            throw new IllegalArgumentException("Can't find template context " + template_context);
            //context = servlet_context;
        }else
            System.out.println("Using template: " + template_context + " " + context.getRealPath("/"));


        /* register default view builder */
        ViewFactoryStore builder_store = new ViewFactoryStore();
        String p = context.getRealPath("/");
        if(p==null) throw new IllegalStateException("Can't get physical path for context "+context.getServletContextName());
        File path = new File(p);
        try{
            ViewConfig   view_config = new ViewConfig(template_context, path);
            builder_store.setViewConfig(view_config);
        } catch(FileNotFoundException ex){
        }
        
        JspViewFactory      jsp_builder = new JspViewFactory(context, template_context);
        ClassViewFactory    builtin = BuiltInViewFactory.createBuiltInViewBuilder();
        DefaultViewFactory  default_builder = new DefaultViewFactory();
        
        builder_store.registerViewFactory( jsp_builder );
        builder_store.registerViewFactory( builtin);        
        builder_store.registerViewFactory( default_builder);
        builder_store.setDefaultFactory(default_builder);
        app.setViewBuilderStore(builder_store);

  }
    
    public void init(ServletContext servlet_context) 
        throws Exception
    {
        System.out.println("***** Application " + servlet_context.getResource("/").toExternalForm() + " starting up *****");
        System.out.println("LWAP core version "+Version.getVersion());        
        String app_path = servlet_context.getRealPath("");
        String config_file = "WEB-INF/application.xml";   

        application = new WebApplication(app_path,config_file,servlet_context);
        CompositeMap application_conf =  application.getApplicationConfig();
        create_view_builder_store(application, servlet_context);

        servlet_context.setAttribute(KEY_APPLICATION, application);        
        initUncertain(servlet_context);
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
    
    public UncertainEngine getUncertainEngine(){
        return uncertainEngine;
    }
    
    public WebApplication getApplication(){
        return application;
    }

    public void contextDestroyed(ServletContextEvent event) {
        application.shutdown();
    }

    public void contextInitialized(ServletContextEvent event) {
        try{
            ServletContext context = event.getServletContext();
            init(context);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
