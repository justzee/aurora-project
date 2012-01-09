package org.lwap.plugin.ntlm;

import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.smb.NtlmPasswordAuthentication;

import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.application.event.SessionController;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

public class NtlmLogin extends AbstractServiceHandle {
	IObjectRegistry mObjectRegistry;
	NtlmConfig ntlmConfig;
	ILogger mLogger;

	public NtlmLogin(IObjectRegistry registry) {
		mObjectRegistry = registry;	
	}

	public int handleEvent(int sequence, CompositeMap context,
			Object[] parameters) throws Exception {	
		ntlmConfig = (NtlmConfig) mObjectRegistry.getInstanceOfType(NtlmConfig.class);
		mLogger = LoggingContext.getLogger("org.lwap.plugin.ntlm",mObjectRegistry);
		MainService service = MainService.getServiceInstance(context);
		HttpServletRequest httpRequest = service.getRequest();
		String msg=httpRequest.getHeader("Authorization");
		boolean is_access=true;
		is_access=checkSession(service);
		if (is_access) {
			if(context.getObject("/cookie/@JSID/@value")!=null&&!"Y".equals(context.getObject("/cookie/@IS_NTLM/@value"))){
				//如果超时，且没有域登陆过，跳过验证
				return EventModel.HANDLE_NORMAL;
			}
			CompositeMap root=new CompositeMap("root");			
			CompositeMap parameter=root.createChild("parameter");
			CompositeMap model=root.createChild("model");	
			String serviceName = (String)context.getObject("/request/@url");
			if(msg==null||!msg.startsWith("NTLM")){							
				parameter.putString("service_name", serviceName);
				service.databaseAccess(ntlmConfig.getChecksql(), parameter, model);			
				
				if ("0".equalsIgnoreCase(TextParser.parse(ntlmConfig.getChecksql_result(), model))){
					mLogger.info(serviceName+" is not login required");
					//如果不需要权限验证跳过域验证
					return EventModel.HANDLE_NORMAL;
				}
			}
			NtlmPasswordAuthentication ntlm = authenticate(context);
			if(ntlm==null)
				return EventModel.HANDLE_NORMAL;			
		
			String username=ntlm.getUsername();		
	
			parameter.put("user_name", username.toUpperCase());
			root.putObject("/request/@address", context.getObject("/request/@address"),true);
			root.putObject("/cookie/@JSID/@value", context.getObject("/cookie/@JSID/@value"),true);			
			mLogger.info("username:"+username);
			mLogger.info("excute procedure "+ntlmConfig.getProcedure());
			service.databaseAccess(ntlmConfig.getProcedure(), parameter, context);	
		}else{
			if ("POST".equals(httpRequest.getMethod().toUpperCase())) {
				if(msg!=null&&msg.startsWith("NTLM"))
					authenticate(context);
			}
		}		
		return EventModel.HANDLE_NORMAL;
	}

	boolean checkSession(MainService service) throws ServletException {
		CompositeMap root=new CompositeMap("root");
		CompositeMap parameter=root.createChild("parameter");
		CompositeMap model=root.createChild("model");		
		CompositeMap context=service.getModel();
		root.putObject("/cookie/@JSID/@value", context.getObject("/cookie/@JSID/@value"),true);	
		root.putObject("/request/@url", context.getObject("/request/@url"),true);	
		root.putObject("/request/@server_name", context.getObject("/request/@server_name"),true);	
		root.putObject("/request/@server_port", context.getObject("/request/@server_port"),true);	
		service.databaseAccess(ntlmConfig.getChecksession(), parameter, model);		
		return "N".equals(model.getObject(ntlmConfig.getChecksession_result())) ? true : false;
	}
	
	NtlmPasswordAuthentication authenticate(CompositeMap context){
		NtlmPasswordAuthentication ntlm = null;
		mLogger = LoggingContext.getLogger("org.lwap.plugin.ntlm",mObjectRegistry);
		MainService service = MainService.getServiceInstance(context);
		HttpServletRequest httpRequest = service.getRequest();
		HttpServletResponse httpResponse = service.getResponse();
		try {
			if ((ntlm = new NtlmAuthenticator(ntlmConfig).authenticate(httpRequest,
					httpResponse)) == null) {
				SessionController state = SessionController
						.createSessionController(context);
				state.setContinueFlag(false);
				return null;
			}
		} catch (Exception e) {
			mLogger.log(Level.SEVERE,"NTLM authenticate fail",e);
			//域验证不通过，跳入普通处理方式
			return null;
		}
		mLogger.log(Level.INFO, "NTLM authenticate domain:"+ntlm.getDomain()+";Username:"+ntlm.getUsername()+";name:"+ntlm.getName());
		return ntlm;
	}
}
