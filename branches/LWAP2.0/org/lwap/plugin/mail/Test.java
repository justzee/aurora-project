/*
 * Created on 2007-6-14
 */
package org.lwap.plugin.mail;

import javax.servlet.http.HttpServletRequest;

import org.lwap.controller.FormController;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

public class Test {
    
    public void onValidateInput(ProcedureRunner runner)
        throws Exception
    {
        CompositeMap context = runner.getContext();
        HttpServletRequest request = (HttpServletRequest)context.get("httprequest");
        String value = request.getParameter("body");        
        /*
        if(value==null || value.length()==0)
            throw new NoPrivilegeException("NoPrivilegeException");
        else
            System.out.println("checked");
            */
        if(!"test".equals(value)){
            context.put(FormController.KEY_PARAMETER_VALID, new Boolean(false));
        }
    }
    

}
