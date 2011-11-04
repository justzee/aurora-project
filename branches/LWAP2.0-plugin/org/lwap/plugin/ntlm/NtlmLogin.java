package org.lwap.plugin.ntlm;

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
		HttpServletResponse httpResponse = service.getResponse();
		
		if (!checkSession(service)) {
			String username;
			CompositeMap parameter=service.getParameters();
			CompositeMap model=service.getModel();		
		
			String serviceName = httpRequest.getRequestURI().substring(
					httpRequest.getContextPath().length() + 1);
			
			parameter.putString("service_name", serviceName);
			service.databaseAccess(ntlmConfig.getChecksql(), parameter, model);			
			mLogger.info("context:"+context.toXML());
			
			if ("0".equalsIgnoreCase(TextParser.parse(ntlmConfig.getChecksql_result(), context))){
				mLogger.info(serviceName+" is not login required");
				//如果不需要权限验证跳过域验证
				return EventModel.HANDLE_NORMAL;
			}
			NtlmPasswordAuthentication ntlm = null;
			try {
				if ((ntlm = new NtlmAuthenticator(ntlmConfig).authenticate(httpRequest,
						httpResponse)) == null) {
					SessionController state = SessionController
							.createSessionController(context);
					state.setContinueFlag(false);
					return EventModel.HANDLE_STOP;
				}
			} catch (Exception e) {
				//域验证不通过，跳入普通处理方式
				return EventModel.HANDLE_NORMAL;
			}
			username=ntlm.getUsername();
			parameter.put("user_name", username.toUpperCase());
			mLogger.info("username:"+username);
			mLogger.info("excute procedure "+ntlmConfig.getProcedure());
			service.databaseAccess(ntlmConfig.getProcedure(), parameter, model);			
			mLogger.info("doLogin context:"+context.toXML());
		}
		
		return EventModel.HANDLE_NORMAL;
	}

	boolean checkSession(MainService service) throws ServletException {
		CompositeMap parameter = service.getParameters();
		CompositeMap model = service.getModel();
		parameter.put("encrypted_session_id", service.getServiceContext()
				.getObject("/cookie/@JSID/@value"));
		service.databaseAccess(ntlmConfig.getChecksession(), parameter, model);
		return ((CompositeMap) model.getObject(ntlmConfig
				.getChecksession_result())).getChilds() == null ? false : true;
	}
}
