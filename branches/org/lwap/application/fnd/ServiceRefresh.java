/*
 * Created on 2005-10-23
 */
package org.lwap.application.fnd;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.javautil.file.WildcardFilter;
import org.lwap.application.ApplicationInitializeException;
import org.lwap.application.WebApplication;
import org.lwap.controller.AbstractController;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;

/**
 * ServiceRefresh
 * @author Zhou Fan
 * 
 */
public class ServiceRefresh extends AbstractController {

    /**
     * @param engine
     */
    public ServiceRefresh(UncertainEngine engine) {
        super(engine);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.lwap.controller.IController#detectAction(javax.servlet.http.HttpServletRequest, uncertain.composite.CompositeMap)
     */
    public int detectAction(HttpServletRequest request, CompositeMap context) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    public void onCreateModel() throws ServletException, ApplicationInitializeException {
       File baseDir = new File (
               ((WebApplication) ServiceInstance.getApplication()).getCompositeLoader().getBaseDir()
               );
       WildcardFilter filter = new WildcardFilter("*.service");
       File[] services = baseDir.listFiles(filter);
       CompositeMap params = new CompositeMap("param");
       for( int i=0; i<services.length; i++){
           String name = services[i].getName();
           name = name.substring(0,name.lastIndexOf('.'));
           params.put("service_name", name);
           //System.out.println(name);
           ServiceInstance.databaseAccess("fnd_service_load.data", params, params);
       }
       
       WebApplication app = (WebApplication)ServiceInstance.getApplication();
       new ApplicationInitializer().initApplication(app,ServiceInstance.getApplication().getApplicationConfig());
       app.getCompositeLoader().clearCache();
       System.out.println("OK!");
       
    }

}
