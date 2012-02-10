/*
 * Created on 2008-12-2
 */
package org.lwap.application.fnd;

import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.controller.MainService;
import org.lwap.feature.SessionCopy;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;

public class SessionPrepare extends AbstractServiceHandle{
    
    public int handleEvent(int sequence, CompositeMap context,
            Object[] parameters) throws Exception 
    {
        MainService service = MainService.getServiceInstance(context);
        CompositeMap service_context = service.getServiceContext();
        SessionCopy.copySession( service.getRequest(), service.getSession() );
        
        return EventModel.HANDLE_NORMAL;        
    }


}
