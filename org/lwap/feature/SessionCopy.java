/*
 * Created on 2008-6-25
 */
package org.lwap.feature;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.service.ServiceContext;

public class SessionCopy implements ISingleton {
    
    public static void copySession( HttpServletRequest request, CompositeMap session_map ) {
        

        HttpSession session = request.getSession(false);
        if(session!=null){
            Enumeration e = session.getAttributeNames();
            while( e.hasMoreElements() ){
                String name = e.nextElement().toString();
                Object value = session.getAttribute(name);
                session_map.put(name, value);
            }
        }
        
    }
    
    public SessionCopy(){
        
    }
    
    public void onBeginService( ServiceContext ctx ) throws Exception {
        MainService svc = MainService.getServiceInstance(ctx.getObjectContext());
        if(svc==null) throw new IllegalStateException("MainService instance not registered in service context");
        CompositeMap session_map = svc.getSession();
        HttpServletRequest request = svc.getRequest();
        copySession(request, session_map);
    }

}
