/*
 * Created on 2005-10-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.lwap.application.FormBasedService;
import org.lwap.validation.ValidationException;

import uncertain.composite.CompositeMap;

/**
 * @author Jian
 *
 */
public class SelectRole extends FormBasedService {
    
    //public static final String KEY_ROLE = "role_id";
	//public static final String KEY_USER_SESSION_ID = "session_id";	    
    int role = 0;
    long region = 0;
    
    public void onValidateInput(CompositeMap parameters)
    throws ValidationException{
        role = ((Number)parameters.get(SessionInitializer.KEY_ROLE_ID)).intValue();
    }

public void onFormPost()
    throws ServletException, IOException
{
    super.onFormPost();
/*
    CompositeMap m = getModel().getChild("REGION");
    if(m!=null) region = m.getLong("REGION_ID",0);
    if(region==0) throw new ServletException("region_id not defined for role assign");
  */  
    HttpSession session = super.request.getSession();
    session.setAttribute(SessionInitializer.KEY_ROLE_ID, new Integer(role));
    
    CompositeMap params = getServiceContext().getChild("session");
    params.put("ip", getRequest().getRemoteAddr());
    params.put(SessionInitializer.KEY_ROLE_ID, new Integer(role));
    this.databaseAccess("SessionInit.data",params,params);
    //System.out.println(params.toXML());
    Number session_id = (Number)params.get("session_id");
    if( session_id == null) throw new IllegalStateException("can't get session id");
    session.setAttribute(SessionInitializer.KEY_SESSION_ID, session_id);

    
}

}
