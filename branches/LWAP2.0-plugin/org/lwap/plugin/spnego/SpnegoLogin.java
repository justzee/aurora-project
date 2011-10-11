package org.lwap.plugin.spnego;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ietf.jgss.GSSException;
import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.event.EventModel;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;

public class SpnegoLogin extends AbstractServiceHandle {
	SpnegoConfig config;
	IObjectRegistry mObjectRegistry;
	public SpnegoLogin(IObjectRegistry registry) {		
		mObjectRegistry=registry;
	}	
	
	@Override
	public int handleEvent(int sequence, CompositeMap context,
			Object[] parameters) throws Exception {
		this.config = (SpnegoConfig)mObjectRegistry.getInstanceOfType(SpnegoConfig.class);	
		ILogger mLogger = LoggingContext.getLogger("org.lwap.plugin.spnego",mObjectRegistry);
		MainService service = MainService.getServiceInstance(context);
		HttpServletRequest httpRequest = service.getRequest();
		String username;
		CompositeMap parameter=service.getParameters();
		CompositeMap model=service.getModel();		
		
		if (!checkSession(service)) {
			String serviceName = httpRequest.getRequestURI().substring(
					httpRequest.getContextPath().length() + 1);
			
			parameter.putString("service_name", serviceName);
			service.databaseAccess(config.getChecksql(), parameter, model);			
			mLogger.info("context:"+context.toXML());
			
			if ("0".equalsIgnoreCase(TextParser.parse(config.getChecksql_result(), context))){
				mLogger.info(serviceName+" is not login required");
				return EventModel.HANDLE_STOP;
			}

			SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse(
					(HttpServletResponse) service.getResponse());
			// client/caller principal

			final SpnegoPrincipal principal;
			try {
				principal = config.getSpnegoAuthenticator().authenticate(
						httpRequest, spnegoResponse);

			} catch (GSSException gsse) {
				// LOGGER.severe("HTTP Authorization Header="
				// + httpRequest.getHeader(Constants.AUTHZ_HEADER));
				throw new Exception(gsse);
			}

			// context/auth loop not yet complete
			if (spnegoResponse.isStatusSet()) {				
				return EventModel.HANDLE_STOP;
			}

			// assert
			if (null == principal) {
				spnegoResponse.setStatus(
						HttpServletResponse.SC_INTERNAL_SERVER_ERROR, true);
				return EventModel.HANDLE_STOP;
			}
			httpRequest = new SpnegoHttpServletRequest(httpRequest, principal);
			username = httpRequest.getRemoteUser();	
			parameter.put("user_name", username.toUpperCase());
			mLogger.info("username:"+username);
		
			mLogger.info("excute procedure "+config.getProcedure());
			service.databaseAccess(config.getProcedure(), parameter, model);			
			mLogger.info("doLogin context:"+context.toXML());
		}
		return EventModel.HANDLE_NORMAL;
	}
	boolean checkSession(MainService service) throws ServletException{
		CompositeMap parameter=service.getParameters();
		CompositeMap model=service.getModel();		
		parameter.put("encrypted_session_id", service.getServiceContext().getObject("/cookie/@JSID/@value"));		
		service.databaseAccess(config.getChecksession(), parameter, model);	
		return ((CompositeMap)model.getObject(config.getChecksession_result())).getChilds()==null?false:true;
	}	
}
