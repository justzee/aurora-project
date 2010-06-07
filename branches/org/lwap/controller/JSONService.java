/*
 * Created on 2008-6-18
 */
package org.lwap.controller;

import javax.servlet.http.HttpServletRequest;

import uncertain.composite.CompositeMap;
import aurora.service.json.JSONDispatcher;

public class JSONService extends JSONDispatcher implements IController {

    public JSONService() {
        super();
    }

    public int detectAction(HttpServletRequest request, CompositeMap context) {
       return IController.ACTION_DETECTED;
    }

    public String getProcedureName() {
        return "org.lwap.controller.InvokeService";
    }

    public void setServiceInstance(MainService service_inst) {
        
    }

}
