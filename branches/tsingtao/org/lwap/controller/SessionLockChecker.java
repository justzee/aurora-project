/*
 * Created on 2011-5-25 ����10:06:28
 * $Id$
 */
package org.lwap.controller;

import uncertain.core.IGlobalInstance;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.ISingleton;
import aurora.service.IServiceSessionLock;
import aurora.service.ServiceSessionLock;

public class SessionLockChecker implements IGlobalInstance, ISingleton {
    
    IServiceSessionLock      mSessionLock;
    IObjectRegistry          mObjectRegistry;
    
    boolean                 defaultCheckAll = false;
    String                  sessionKey = "${/session/@session_id}";
    String                  errorMessage = "PROMPT.SERVICE_IN_LOCK";
    
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean getDefaultCheckAll() {
        return defaultCheckAll;
    }

    public void setDefaultCheckAll(boolean defaultCheckAll) {
        this.defaultCheckAll = defaultCheckAll;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String key) {
        this.sessionKey = key;
    }
    
    /**
     * @param reg
     */
    public SessionLockChecker(IObjectRegistry reg) {
        super();
        this.mObjectRegistry = reg;
    }
    
    public IServiceSessionLock getServiceSessionLock(){
        return mSessionLock;
    }
    
    public void onInitialize(){
        mSessionLock = (IServiceSessionLock)mObjectRegistry.getInstanceOfType(IServiceSessionLock.class);
        if(mSessionLock==null){
            mSessionLock = new ServiceSessionLock();
            mObjectRegistry.registerInstance(IServiceSessionLock.class, mSessionLock);
        }
        mObjectRegistry.registerInstance(SessionLockChecker.class, this);
    }
     

}
