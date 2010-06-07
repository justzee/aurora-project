/**
 * Created on: 2002-11-12 13:39:38
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;



public interface ServiceParticipant {
	
	public static final int SERVICE_CONTINUE			=0;
	public static final int BREAK_PRE_SERVICE_LIST	=1;
	public static final int BREAK_WHOLE_SERVICE		=-1;

//	public void setApplicationConfig( CompositeMap app_conf);	
	
	public void init( CompositeMap params );
	
	public int service(HttpServletRequest request, HttpServletResponse response, Service service )
				  throws IOException, ServletException;	
	
}
