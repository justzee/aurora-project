/*
 * Created on 2005-10-10
 */
package org.lwap.controller;

import uncertain.core.UncertainEngine;

/**
 * AbstractController
 * @author Zhou Fan
 * 
 */
public abstract class AbstractController implements IController {
    
    protected UncertainEngine			uncertainEngine;
    public MainService		            ServiceInstance;
    
    String			procedureName;

    /**
     * get UncertainEngine from framework
     */
    public AbstractController(UncertainEngine engine) {
        uncertainEngine = engine;
    }

    /**
     * @return Returns the procedureName.
     */
    public String getProcedureName() {
        return procedureName;
    }
    /**
     * @param procedureName The procedureName to set.
     */
    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }
    /**
     * @return Returns the serviceInstance.
     */
    public MainService getServiceInstance() {
        return ServiceInstance;
    }
    /**
     * @param serviceInstance The serviceInstance to set.
     */
    public void setServiceInstance(MainService serviceInstance) {
        this.ServiceInstance = serviceInstance;
    }
}
