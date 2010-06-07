/*
 * Created on 2009-6-16
 */
package org.lwap.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.ocm.IConfigurable;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class CookieOperate extends AbstractEntry implements IConfigurable {
    
    public static final String KEY_SET_COOKIE = "set-cookie";

    String      name;
    String      value;
    int         maxAge = -1;
    
    String      mUsage;
    
    public void beginConfigure(CompositeMap config){
        mUsage = config.getName();
    }
    
    public void endConfigure(){
        
    }
    
    protected void doSetCookie(ProcedureRunner runner){
        CompositeMap    context = runner.getContext();
        MainService svc = MainService.getServiceInstance(context);
        if(name==null)
            throw new ConfigurationError("Must set 'name' property");
        if(value==null)
            throw new ConfigurationError("Must set 'value' property");
        name = TextParser.parse(name, context);
        value = TextParser.parse(value, context);
        HttpServletResponse response = svc.getResponse();
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);        
    }

    public void run(ProcedureRunner runner) throws Exception {
        if(KEY_SET_COOKIE.equalsIgnoreCase(mUsage))
            doSetCookie(runner);        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

}
