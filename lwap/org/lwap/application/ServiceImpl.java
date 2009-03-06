/**
 * Created on: 2002-11-12 13:15:16
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;

public abstract class ServiceImpl implements Service {

	private static final String KEY_REQUEST = "request";
    private static final String KEY_ADDRESS = "address";
    //protected CompositeMap application_config;
	protected WebApplication 			application;
	protected CompositeMap 			service_properties;
	protected CompositeMap 			service_context;
	protected HttpServletRequest		request;
	protected HttpServletResponse		response;
	protected HttpServlet 			servlet;		

	// name of this service;
	String		ServiceName;
	
	// result of service, used in service dispatch
	String		ServiceResult;

	public void setApplication( Application app ){
		application = (WebApplication)app;
	}
	
	public Application getApplication(){
		return application;
	}
	

	public CompositeMap getApplicationConfig(){
		return application.getApplicationConfig();
	}

	public void setServiceContext( CompositeMap context){
		service_context = context;	
	}	

	public CompositeMap getServiceConfig(){
		return service_properties;
	}


	public void init( CompositeMap params ){
		service_properties = params;
	}

	public CompositeMap getServiceContext(){
		return service_context;
	}	
	
	public void setHttpObject(HttpServletRequest request,	HttpServletResponse response ){
		this.request = request;
		this.response = response;
        CompositeMap r = service_context.createChild(KEY_REQUEST);
        r.put(KEY_ADDRESS, request.getRemoteAddr());
	}
	
	public HttpServletRequest getRequest(){
		return this.request;
	}
	
	public HttpServletResponse getResponse(){
		return this.response;
	}
	
	public HttpServlet getServlet(){
		return this.servlet;
	}
	
	public void setServlet(HttpServlet svlt){
		this.servlet = svlt;
	}
	

	public abstract void service(
			HttpServlet			servlet, 
			HttpServletRequest  request,
			HttpServletResponse response)
		throws IOException, ServletException;
	/**
	 * Returns the serviceName.
	 * @return String
	 */
	public String getServiceName() {
		return ServiceName;
	}

	/**
	 * Sets the serviceName.
	 * @param serviceName The serviceName to set
	 */
	public void setServiceName(String serviceName) {
		ServiceName = serviceName;
	}
	

	/**
	 * Returns the serviceResult.
	 * @return String
	 */
	public String getServiceResult() {
		return ServiceResult;
	}

	/**
	 * Sets the serviceResult.
	 * @param serviceResult The serviceResult to set
	 */
	public void setServiceResult(String serviceResult) {
		ServiceResult = serviceResult;
	}
	
	public void finish(){
		if( service_properties != null) service_properties.clear();
		if( service_context != null ) service_context.clear();
		servlet = null;
		application = null;
	}

}
