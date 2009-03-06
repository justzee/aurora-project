/**
 * Created on: 2002-11-11 17:42:26
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uncertain.composite.CompositeMap;

public class PolyStateService extends BaseService {
	
	public static final String ELEMENT_STATE = "state";
	public static final String KEY_STATE_NAME = "name";	
	public static final String KEY_STATE_PARAMETER = "state-parameter";

	
	HashMap		existing_states = new HashMap(20);
//	static HashMap method_cache = new HashMap();
	
	public String state;
	
	/** get current service state */
	public String getState(){
		return state;
	}
	
	/** set current service state */
	public void setState(String _state){
		state = _state;
	}
	
	/** override this method to detect actual state */
	public void detectServiceState() throws InvalidInvokeException,IOException{
		String state_param = this.getBaseServiceConfig().getString(KEY_STATE_PARAMETER);
		if( state_param != null){
			state =  this.request.getParameter(state_param);
			if( state == null) throw new InvalidInvokeException("No state specified");
		}
	}
	
	/** load multiple service config for different states */	
	public void initStateTable()  throws IOException, ServletException {
		CompositeMap config = this.getBaseServiceConfig();
		Iterator childs = config.getChildIterator();
		if( childs == null) return;
		while( childs.hasNext()){
			CompositeMap child = (CompositeMap) childs.next();
			if( child.getName().equals(ELEMENT_STATE) && child.get(KEY_STATE_NAME) != null){
				existing_states.put(child.get(KEY_STATE_NAME), child);
			}
		}
	}	
	
	
	/** return proper service config for current state*/
	public CompositeMap getServiceConfig(){
		String _state = getState();
		if( _state != null){
			Object state_config = existing_states.get(_state);
			return state_config == null? super.getServiceConfig(): (CompositeMap)state_config;
		} else
			return super.getServiceConfig();	
	}
	
	
	
	
	/** get a parameter from service config */
	public Object getServiceParameter(String key){
		Object obj = getServiceConfig().get(key);
		if( obj == null) obj = this.getBaseServiceConfig().get(key);
//		System.out.println("getServiceParameter:" + key+"="+obj);
		return obj;
	}	
	
	
	/** get a sub section from service config of current state.
	 *  if not found, return section in base service config
	 *  @param section_name name of section, such as "model", "view"
	 */
	public CompositeMap getServiceConfigSection(String section_name){		
		//return getServiceConfig().getChild(section_name);
		CompositeMap section = this.getServiceConfig().getChild(section_name);
		if( section != null) return section;
		else return  getBaseServiceConfig().getChild(section_name);
	}	
	
	/** get root service config that be same for all states*/
	public CompositeMap getBaseServiceConfig(){
		return super.getServiceConfig();
	}
	

	/** override to call detectServiceState() prior to super.service() */
	public void service(HttpServlet servlet, HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
	{
		try{
			super.initService(servlet, request,response);
			initStateTable();
			detectServiceState();			
		} catch( InvalidInvokeException ex){
			throw new ServletException(ex);
		}
			
		CompositeMap state_config = (CompositeMap)this.existing_states.get(getState());
		if( state_config != null)
			try{
				if( state_config.containsKey("class")){
					Service svc = application.getService(state_config);
					svc.setServiceContext(this.getServiceContext());
					svc.service(servlet, request,response);
				}else
					super.service(servlet, request,response);
			} catch (ServiceInstantiationException ex){
				throw new ServletException(ex.getCause());
		} else{	
			//throw new ServletException(new InvalidInvokeException("Can't find state:"+getState()));
			super.service(servlet, request,response);
		}
	}
	
	
	Method getStateMethod( String method_name){
		Class cls = this.getClass();
		String method = method_name + getState();
		try{
			return cls.getMethod(method,null);
		}catch(Exception ex){
			return null;
		}
	}

/*	
	void invokeByReflection(String method_name, Object[] params) throws ServletException {
		
	}
*/

	/* ------------- BEGIN overrided behavior -------------------- */
	
	public void parseParameter()throws IOException, ServletException{
		Method m = getStateMethod("parseParameter");
		if( m != null)
		try{
			m.invoke(this,null);
		}catch(IllegalAccessException ex){
			throw new ServletException(ex);
		}catch(InvocationTargetException iex){
			throw new ServletException(iex.getCause());
		}
		else
			super.parseParameter();
	}

/*
	public void preService()
				  throws IOException, ServletException{
		Method m = getStateMethod("preService");
		if( m != null)
		try{
			m.invoke(this,null );
		}catch(IllegalAccessException ex){
			throw new ServletException(ex);
		}catch(InvocationTargetException iex){
			throw new ServletException(iex.getCause());
		}
		else
			super.preService();
	}
*/		

	public void checkPrivilege() throws IOException, NoPrivilegeException {
		Method m = getStateMethod("checkPrivilege");
		if( m != null)
		try{
			m.invoke(this,null );
		}catch(IllegalAccessException ex){
			throw new NoPrivilegeException(ex);
		}catch(InvocationTargetException iex){
			throw new NoPrivilegeException(iex.getCause());
		}
		else
			super.checkPrivilege();

	}	
	
	public void createModel() throws  IOException,ServletException{
		Method m = getStateMethod("createModel");
		if( m != null)
		try{
			m.invoke(this,null );
		}catch(IllegalAccessException ex){
			throw new ServletException(ex);
		}catch(InvocationTargetException iex){
			throw new ServletException(iex.getCause());
		}
		else
			super.createModel();

	}

	public void createView() throws IOException,ServletException
	{
		Method m = getStateMethod("createView");
		if( m != null)
		try{
			m.invoke(this ,null);
		}catch(IllegalAccessException ex){
			throw new ServletException(ex);
		}catch(InvocationTargetException iex){
			throw new ServletException(iex.getCause());
		}
		else
			super.createView();
	}
	

	public void doService() throws  IOException,ServletException{
		Method m = getStateMethod("doService");
		if( m != null)
		try{
			m.invoke(this ,null);
		}catch(IllegalAccessException ex){
			throw new ServletException(ex);
		}catch(InvocationTargetException iex){
			//iex.printStackTrace();
			throw new ServletException(iex.getCause());
		}
		else{
			super.doService();
/*
			System.out.println("Poly.doService");
			System.out.println("super.doService");
*/			
		}
	}
	
	/* ------------- END overrided behavior -------------------- */	
	
	

}
