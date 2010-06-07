/*
 * Created on 2005-10-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.lwap.application.BaseService;
import org.lwap.application.Service;
import org.lwap.application.ServiceParticipant;
import org.lwap.application.WebApplication;
import org.lwap.feature.SessionCopy;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;


/**
 * @author Jian
 *
 */
public class SessionInitializer
    implements ServiceParticipant
{
public static final String KEY_NO_PRIVILEDGE_URL = "no-priviledge-url";
/*
    public static final String KEY_LOGIN_REQUIRED = "login-required";
    public static final String KEY_ACCESS_CHECKED = "access-checked";
*/
    public static final String KEY_LOGIN_REQUIRED = "IS_LOGIN_REQUIRED";
    public static final String KEY_ACCESS_CHECKED = "IS_ACCESS_CHECKED";
    
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ROLE_ID = "role_id";
    public static final String KEY_LOCALE_ID = "locale_id";
    public static final String KEY_SESSION_ID = "session_id";    
    
    public static final String KEY_CAN_ACCESS = "can_access";
/*
    public static final String SESSION_PARAMETERS[] = {
            KEY_USER_ID,KEY_ROLE_ID,KEY_LOCALE_ID, KEY_SESSION_ID
    };
*/

    public static String getNoAccessURL( BaseService s, String function_code_path ){
        String url = s.getApplication().getApplicationConfig().getString(KEY_NO_PRIVILEDGE_URL,"fnd_no_priviledge.service?function="+function_code_path);
        url = TextParser.parse(url, s.getServiceContext());
        return url;
    }

    public SessionInitializer()
    {
    }

    public void init(CompositeMap compositemap)
    {
    }
    
	int redirectLogin(HttpServletRequest  request,
			HttpServletResponse response) throws IOException {
				String url = request.getRequestURI();
				String qs = request.getQueryString();
				if( qs != null) url += ('?'+qs);
				url = response.encodeURL(url);
				response.sendRedirect("fnd_session_expire.service?url=" + url );
				return ServiceParticipant.BREAK_WHOLE_SERVICE;
	}

    

    boolean checkSessionParam(String key, HttpSession session, CompositeMap session_context)
    {
        Object obj = session.getAttribute(key);
        if(obj == null)
        {
            System.out.println("Key is :" + key);
            return false;
        } else
        {
            session_context.put(key, obj);
            return true;
        }
    }
    
    void loadServiceConfig(){
        
    }
    
    public int service(HttpServletRequest request, HttpServletResponse response, Service service)
    throws ServletException, IOException
    {
       

        
        if(!(service instanceof BaseService)) return 0;
        BaseService s = (BaseService)service;

        // copy all session fields into context's session part
        CompositeMap service_context = s.getServiceContext();
        SessionCopy.copySession( request, s.getSession() );
        // get service name
        String service_name = s.getServiceName();
        service_name = service_name.substring(0,service_name.indexOf("."));
        service_name = service_name.toLowerCase();        
        // load service config
        WebApplication app = (WebApplication)s.getApplication();
        CompositeMap serviceMap = (CompositeMap)(app.getApplicationConfig().get(ApplicationInitializer.SERVICE_MAP));
        CompositeMap serviceConfig = (CompositeMap)serviceMap.get(service_name);
        if(serviceConfig !=null)
            s.getServiceContext().addChild(serviceConfig);
        else
            throw new ServletException("Service:"+service_name +" not defined in database");
        // check login
        // System.out.println("[Session] Checking " + service_name );
        int login_required = serviceConfig.getInt(KEY_LOGIN_REQUIRED, 1 );
        HttpSession session = request.getSession(false);
        if(login_required==1){
            // System.out.println("[Session] Login required");
            if(session == null) {
                return redirectLogin(request, response);
            }        
            if(session.getAttribute(KEY_USER_ID)==null) 
                return redirectLogin(request, response);
        }

        // transfer session
/*    
        if(session!=null){
	        //CompositeMap session_context = s.getSession();
	        CompositeMap sessionContext = s.getSession();
	
	        for(int i = 0; i < SESSION_PARAMETERS.length; i++) {
	            sessionContext.put(SESSION_PARAMETERS[i], session.getAttribute(SESSION_PARAMETERS[i]));
	        }
        }
*/

        // check role access
        int access_checked = serviceConfig.getInt(KEY_ACCESS_CHECKED, 0);
        if(access_checked==1){
            s.databaseAccess("fnd_access_check.data", service_context, service_context);
	        int can_access = service_context.getInt(KEY_CAN_ACCESS, 0);
	        if(can_access!=1){ 
                //throw new NoPrivilegeException("You can't access this page with current role");
                String url = getNoAccessURL(s, "${/service-config/@FUNCTION_CODE}" );
                response.sendRedirect(url);
                return ServiceParticipant.BREAK_PRE_SERVICE_LIST;
            }
        }        

        return 0;
    }


}
