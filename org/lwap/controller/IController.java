/*
 * Created on 2005-10-9
 */
package org.lwap.controller;

import javax.servlet.http.HttpServletRequest;

import uncertain.composite.CompositeMap;

/**
 * IController
 * @author Zhou Fan
 * 
 */
public interface IController {
    
    public static final int ACTION_DETECTED = 1;
    public static final int ACTION_NOT_DETECTED = 0;
    
    /**
     * Called by framework to detect current action to do
     * @param request
     * @param context
     * @return ACTION_DETECTED if get proper action to do,
     * ACTION_NOT_DETECTED if not
     */
    public int detectAction( HttpServletRequest request, CompositeMap context );
    
    /**
     * Called by framework to get proper procedure name to run
     * @return procedure name
     */
    public String getProcedureName();
    
 
    public void setServiceInstance(MainService service_inst);
}
