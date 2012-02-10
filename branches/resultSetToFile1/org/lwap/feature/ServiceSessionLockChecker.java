/*
 * Created on 2011-5-25 下午09:46:38
 * $Id$
 */
package org.lwap.feature;

import org.lwap.controller.MainService;
import org.lwap.controller.SessionLockChecker;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.TextParser;
import uncertain.event.EventModel;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.service.IServiceSessionLock;
import aurora.service.ServiceContext;
import aurora.service.ServiceSessionLock;
import aurora.service.json.JSONServiceContext;
import aurora.service.validation.ErrorMessage;

public class ServiceSessionLockChecker {

 SessionLockChecker  mLockChecker;
    
    
    public ServiceSessionLockChecker(IObjectRegistry reg) {
        mLockChecker = (SessionLockChecker)reg.getInstanceOfType(SessionLockChecker.class);
    }
    
    public int onBeginService(ProcedureRunner runner)
        throws Exception
    {
        JSONServiceContext ct = (JSONServiceContext)DynamicObject.cast(runner.getContext(), JSONServiceContext.class);
        if(mLockChecker!=null){
            if(doSessionLockCheck(ct)){
                runner.stop();
                return EventModel.HANDLE_STOP;
            }else{
                return EventModel.HANDLE_NORMAL;
            }
        }else
            return EventModel.HANDLE_NORMAL;
    }
    
    /**
     * 
     * @param ct
     * @return true if service is locked, false is service is not locked
     * @throws Exception
     */
    public boolean doSessionLockCheck(ServiceContext ct)
        throws Exception
    {
        MainService svc = MainService.getServiceInstance(ct.getObjectContext());
        CompositeMap svc_config = svc.getServiceConfig();
        String name = svc.getServiceName();
        
        
        Boolean check_lock = svc_config.getBoolean("checkSessionLock");
        if(check_lock==null)
            check_lock = new Boolean(mLockChecker.getDefaultCheckAll());

        
       if(!check_lock.booleanValue())
           return false;
        
       String   lock_key = svc_config.getString("lockKey");
       if(lock_key==null)
           lock_key = mLockChecker.getSessionKey();
       lock_key = TextParser.parse(lock_key, svc.getServiceContext());
       
       String service_name = svc_config.getString("lockService"); 
           if(service_name==null)
               service_name = svc.getServiceName();
           else
               service_name = TextParser.parse(service_name, svc.getServiceContext());
       
       IServiceSessionLock ss_lock = mLockChecker.getServiceSessionLock();
       boolean locked = ss_lock.islocked(lock_key, service_name);
       if(locked){
           ServiceContext svc_context = ServiceContext.createServiceContext(svc.getServiceContext());
           ErrorMessage msg = new ErrorMessage(null,mLockChecker.getErrorMessage(), null);
           svc_context.setError(msg.getObjectContext());
           //TODO raise exception
           //onCreateFailResponse(svc_context);
           return true;
       }else{
           ss_lock.lock(lock_key, service_name, 0);
           ServiceSessionLock.Unlocker unlocker = new ServiceSessionLock.Unlocker(ss_lock, lock_key, service_name);
           svc.addResourceReleaser(unlocker);
           return false;
       }
        
    }        
}
