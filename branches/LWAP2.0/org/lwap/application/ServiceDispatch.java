/**
 * Created on: 2002-11-19 14:22:15
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 *  For include or forward to another service
 */

public class ServiceDispatch {

	
	public static final String KEY_PARAMETER_TRANSFORM = "parameter-transform";
/*
	public static final String KEY_PARAMETER_NAME      = "Name";
*/	
		
	
	public static final int DISPATCH_STYLE_INCLUDE = 0;
	public static final int DISPATCH_STYLE_FORWARD = 1;
	public static final int DISPATCH_STYLE_REDIRECT = 2;
	
	public static final String[] DISPATCH_STYLE_NAMES = {"include","forward","redirect"};

	public static final int TARGET_TYPE_SERVICE = 0;
	public static final int TARGET_TYPE_URL = 1;		
	
	public static final String[] TARGET_TYPE_NAMES = {"service", "url"};	
	
	static int getTypeName( String[] array, String input){
		if( input == null) return -1;
		for( int n=0; n<array.length; n++){
			if( array[n].equalsIgnoreCase(input)) return n;
		}
		return -1;
	}
	
	public static int getDispatchStyle( String type_name){
		return getTypeName(DISPATCH_STYLE_NAMES, type_name);
	}

	public static int getTargetType( String type_name){
		return getTypeName(TARGET_TYPE_NAMES, type_name);
	}
		
	int dispatch_style = DISPATCH_STYLE_FORWARD;
	
	int target_type = DISPATCH_STYLE_INCLUDE;
	
//	boolean new_context = false;
	
	String dispatch_url;
	
	String service_name;
	
	BaseService origin_service;
	
	CompositeMap	context;
	
	/** creates an dispatch*/
	public ServiceDispatch( BaseService origin_service, int target_type, String target_name, int dispatch_style){
		setDispatchStyle(dispatch_style);
		setOriginService(origin_service);
		setTargetType(target_type);
		if( target_type == TARGET_TYPE_SERVICE){
			setServiceName(target_name);
		}else{
			setDispatchURL(target_name);
		}
		
	}
	
	public void setServiceContext( CompositeMap ct){
		this.context = ct;
	}

/*	
	void transformParameter(CompositeMap view, CompositeMap context){

		CompositeMap param_transform = this.origin_service.getServiceConfig().getChild(KEY_PARAMETER_TRANSFORM);
		if( param_transform == null) return;
		if( param_transform.getChilds() == null) return;

		CompositeMap params = context.getChild(BaseService.KEY_PARAMETER);
		if( params == null) params = context.createChild(BaseService.KEY_PARAMETER);
		
		for( Iterator it = param_transform.getChildIterator(); it.hasNext(); ){
			CompositeMap item = (CompositeMap)it.next();
			String name = item.getString("Name");
			String data_field = item.getString("dataField");
			if( name == null || data_field==null) continue;
			Object obj = context.getObject(data_field);
			if( obj != null) params.put(name,obj);						
		}
		
		System.out.println("after transform:\r\n" + params.toXML());
	}
*/	

/*	
	public void setNewContext( boolean b){
		new_context = b;
	}
*/	
	
	public void dispatch() throws IOException, ServletException {
		WebApplication app = (WebApplication)this.origin_service.getApplication();


		if( getTargetType() == TARGET_TYPE_SERVICE){
			
			String svc_url = app.getServiceURL(this.getServiceName());
			svc_url = TextParser.parse(svc_url, this.origin_service.getServiceContext());			
			
			if( this.dispatch_style ==DISPATCH_STYLE_REDIRECT ){
				this.origin_service.getResponse().sendRedirect(svc_url); 
			}else
				try
				{
/*
					CompositeMap context;
					if( new_context)
		   				context = new CompositeMap();
					else
		   				context = origin_service.getServiceContext();
*/
					if( context == null) context = origin_service.getServiceContext();		   				

//					transformParameter(context);
					BaseService svc = (BaseService)app.getService(this.service_name, context);
					
					// get origin dispatch
					BaseService caller = origin_service.getCallingService();
					svc.setCallingService(origin_service);
					svc.service(origin_service.getServlet(), origin_service.getRequest(),origin_service.getResponse());

					// put origin dispath
					origin_service.setCallingService(caller);
				} catch(Exception ex){
					throw new ServletException( ex.getCause() );
				}
		}else{
			RequestDispatcher disp;
			ServletContext context = origin_service.getServletContext();
			String url = TextParser.parse(this.getDispatchURL(),this.origin_service.getServiceContext());
			
			switch( this.dispatch_style ){
				case ServiceDispatch.DISPATCH_STYLE_REDIRECT:
					origin_service.getResponse().sendRedirect( url );
					break;
				case ServiceDispatch.DISPATCH_STYLE_INCLUDE:					
					disp = context.getRequestDispatcher(url);
					if( disp != null) disp.include( origin_service.getRequest(),origin_service.getResponse() );
					break;				
				case ServiceDispatch.DISPATCH_STYLE_FORWARD:
					disp = context.getRequestDispatcher(url);
					if( disp != null) disp.forward( origin_service.getRequest(),origin_service.getResponse() );				
					break;
			}
		}
	}
	

	/**
	 * Returns the dispatchStyle.
	 * @return int
	 */
	public int getDispatchStyle() {
		return dispatch_style;
	}

	/**
	 * Returns the dispatchURL.
	 * @return String
	 */
	public String getDispatchURL() {
		return dispatch_url;
	}

	/**
	 * Returns the originService.
	 * @return BaseService
	 */
	public BaseService getOriginService() {
		return origin_service;
	}

	/**
	 * Returns the serviceName.
	 * @return String
	 */
	public String getServiceName() {
		return service_name;
	}

	/**
	 * Returns the targetType.
	 * @return int
	 */
	public int getTargetType() {
		return target_type;
	}

	/**
	 * Sets the dispatchStyle.
	 * @param dispatchStyle The dispatchStyle to set
	 */
	public void setDispatchStyle(int dispatchStyle) {
		dispatch_style = dispatchStyle;
	}

	/**
	 * Sets the dispatchURL.
	 * @param dispatchURL The dispatchURL to set
	 */
	public void setDispatchURL(String dispatchURL) {
		dispatch_url = dispatchURL;
		if( this.getTargetType() == TARGET_TYPE_URL && this.getDispatchStyle() != DISPATCH_STYLE_REDIRECT)
			if( dispatch_url.charAt(0) != '/')
		 		dispatch_url = '/' + dispatch_url;
	}

	/**
	 * Sets the originService.
	 * @param originService The originService to set
	 */
	public void setOriginService(BaseService originService) {
		origin_service = originService;
	}

	/**
	 * Sets the serviceName.
	 * @param serviceName The serviceName to set
	 */
	public void setServiceName(String serviceName) {
		service_name = serviceName;
	}

	/**
	 * Sets the targetType.
	 * @param targetType The targetType to set
	 */
	public void setTargetType(int targetType) {
		target_type = targetType;
	}
	
	public static void main(String[] args){
//		System.out.println( getDispatchType("Redirect"));
		System.out.println( getTargetType("url"));
	}

}
