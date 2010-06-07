/*
 * Created on 2005-10-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

/**
 * @author Jian
 *
 */
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.lwap.application.BaseService;
import org.lwap.controller.MainService;
import org.lwap.validation.ValidationException;

import uncertain.composite.CompositeMap;

public class Login extends MainService
{

    public static final String KEY_USER_ROLE = "role_id";
	public static final String KEY_USER_ID = "user_id";	
	public static final String KEY_USER_NAME = "user_name";
	public static final String KEY_USER_PASSWORD = "user_password";
	public static final String KEY_IS_MEMORY = "is_memory";
	public static final String KEY_LOCALE_ID = "locale_id";
	
    public static final int  MAX_AGE = 365*24*60*60;
    public static final Integer DEFAULT_LOCALE_ID = new Integer(1);
        
    String user_name="";
    String user_pwd="";
    int is_memory=0;
//    Number account_id;
        

	static CompositeMap createLocale(int locale_id, String locale_name){
		CompositeMap locale = new CompositeMap("locale");
		locale.put("locale_id",new Integer(locale_id));
		locale.put("locale_name",locale_name);
		return locale;
	}

	static CompositeMap locale = new CompositeMap("locale-list");
	static{
		locale.addChild( createLocale(1,"ÖÐÎÄ"));
		locale.addChild( createLocale(2,"English"));
	}

	public void createModel() throws  IOException,ServletException{
		getModel().addChild(locale);
	}
	
	public static boolean doLogin( 
            HttpServletRequest request, 
            BaseService service, 
            String user_name, 
            String user_pwd, 
            Number locale_id)
    throws ServletException
	{

	    CompositeMap params = new CompositeMap("parameter");
        params.put(Login.KEY_USER_NAME, user_name);
        params.put(Login.KEY_USER_PASSWORD, user_pwd);
        CompositeMap target = service==null?params:service.getModel();
        service.databaseAccess("Login.data",params,target);
        Number account_id = (Number)target.getObject("login-account/@ACCOUNT_ID");
        if( account_id == null) return false;
		if( account_id.intValue() == 0) return false;

		HttpSession session = request.getSession(true);
		//session.setMaxInactiveInterval(3600);
		session.setAttribute(KEY_USER_ID, account_id);

		params.clear();
		
		
		return true;
		  
		
	}
		

	public void onValidateInput() throws InvalidAccountException, ServletException {
	    CompositeMap parameters = super.getParameters();
        user_name = parameters.getString(KEY_USER_NAME);
        user_pwd  = parameters.getString(KEY_USER_PASSWORD);
        //is_memory = ((Number)parameters.get(KEY_IS_MEMORY)).intValue();
        is_memory=0;
		if(!doLogin( this.getRequest(), this, user_name, user_pwd, DEFAULT_LOCALE_ID ))
			throw new InvalidAccountException(user_name);
	}

	public void postDoAction()	{

        if (is_memory==1)
        {
            Cookie cookie=null;
            cookie=new Cookie(KEY_USER_NAME,user_name);
            cookie.setMaxAge(MAX_AGE);
            response.addCookie(cookie);
            cookie=new Cookie(KEY_USER_PASSWORD,user_pwd);
            cookie.setMaxAge(MAX_AGE);
            response.addCookie(cookie);
        }
    }


}
