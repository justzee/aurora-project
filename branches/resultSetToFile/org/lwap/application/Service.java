/**
 * Created on: 2002-11-10 15:20:34
 * Author:     zhoufan
 */
package org.lwap.application;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;



public interface Service {
	
	
	public void setApplication( Application app );
	
	public Application getApplication();


	public void init( CompositeMap params );
	
	public CompositeMap getServiceConfig();

	
	public void setServiceContext( CompositeMap context);	

	public CompositeMap getServiceContext();
	
	public String getServiceName();
	
	public void setServiceName(String name);
	
	public void service(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response )
				  throws Exception;
				  
	public void finish();			
	
	public String getErrorDescription();

}
