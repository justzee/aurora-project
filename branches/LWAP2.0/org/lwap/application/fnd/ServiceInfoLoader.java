/*
 * Created on 2008-12-2
 */
package org.lwap.application.fnd;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;
import org.lwap.application.WebApplication;
import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.controller.MainService;
import org.lwap.feature.SessionCopy;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;

public class ServiceInfoLoader extends SessionPrepare {

    public int handleEvent(int sequence, CompositeMap context,
            Object[] parameters) throws Exception 
    {
        MainService service = MainService.getServiceInstance(context);
        super.handleEvent(sequence, context, parameters);

        // get service name
        String service_name = service.getServiceName();
        int idx = service_name.indexOf(".");
        if(idx>0){
            service_name = service_name.substring(0,idx);
            service_name = service_name.toLowerCase();        
        }
        // load service config
        WebApplication app = (WebApplication)service.getApplication();
        CompositeMap serviceMap = (CompositeMap)(app.getApplicationConfig().get(ApplicationInitializer.SERVICE_MAP));
        CompositeMap serviceConfig = (CompositeMap)serviceMap.get(service_name);
        if(serviceConfig !=null)
            service.getServiceContext().addChild(serviceConfig);
        else
            throw new ServletException("Service:"+service_name +" not defined in database");
        /*
        System.out.println("After session prepare:");
        System.out.println(context.toXML());
        */
        return EventModel.HANDLE_NORMAL;
    }

}
