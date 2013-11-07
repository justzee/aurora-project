/**
 * Created on: 2002-11-12 13:15:16
 * Author:     zhoufan
 */
package org.lwap.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

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
	
	String      error_description;
	
	public static CompositeMap createCookieMap( Cookie cookie ){
	    CompositeMap m = new CompositeMap("cookie");
	    m.put("name", cookie.getName());
	    m.put("value", cookie.getValue());
	    m.put("domain", cookie.getDomain());
	    m.put("path", cookie.getPath());
	    m.putInt("maxage", cookie.getMaxAge());
	    m.putBoolean("secure", cookie.getSecure());
	    return m;
	}
	
	public static void populateCookieMap( HttpServletRequest request, CompositeMap target ){
	    Cookie[] cookies = request.getCookies();
	    if(cookies!=null)
    	    for(int i=0; i<cookies.length; i++){
    	        CompositeMap m = createCookieMap(cookies[i]);
    	        target.put(cookies[i].getName(), m);
    	    }
	}

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
		JSONObject dm=getParameterString(request);
        CompositeMap r = service_context.createChild(KEY_REQUEST);
        String ip = request.getHeader("x-forwarded-for");   
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = request.getHeader("Proxy-Client-IP");   
		}   
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = request.getHeader("WL-Proxy-Client-IP");   
		}   
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
			ip = request.getRemoteAddr();   
		}
        r.put(KEY_ADDRESS, ip);
        r.put("url", getServiceName());
        if(dm.length()!=0){
        	r.put("params", dm);
        }
        r.put("server_name", request.getServerName());
        r.put("server_port", new Integer(request.getServerPort()));
		r.put("context_path", request.getContextPath());
        CompositeMap cookie = service_context.createChild("cookie");
        populateCookieMap(request, cookie);
	}
	public JSONObject getParameterString(HttpServletRequest request){		 
		Map params = request.getParameterMap();
		JSONObject ps=new JSONObject();
		if(params.size()>0){
			Iterator it=params.entrySet().iterator();
			String[] valueHolder = new String[1];
			while(it.hasNext()){
				Map.Entry entry=(Map.Entry)it.next();
				String name=entry.getKey().toString();
				Object value=entry.getValue();
				String[] values;
				if(value instanceof String[]){
					values=(String[])value;
				}else{
					valueHolder[0]=value.toString();
					values=valueHolder;
				}
				try{
					ArrayList al=new ArrayList();			
					for(int i=0;i<values.length; i++){						
						if (values[i] != null) {							
							al.add(values[i]);
						}
					}
					ps.put(name,al);
				}catch(Exception e){
					throw new RuntimeException(e);
				}
			}
		}
		return ps;
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
		throws Exception;
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
	
	public String getErrorDescription(){
	    return error_description;
	}
	
	protected void setErrorDescription( String desc ){
	    this.error_description = desc;
	}

}
