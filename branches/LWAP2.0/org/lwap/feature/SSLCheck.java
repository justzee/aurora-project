package org.lwap.feature;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.application.event.SessionController;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.event.EventModel;

public class SSLCheck extends AbstractServiceHandle {
	
	private static final String URL_HTTPS = "https";
//	private static final int DEFAULT_HTTPS_PORT = 8443;
	
	private SSLCheckConfig config;
	
	public SSLCheck(SSLCheckConfig config){
		if(config == null) throw new RuntimeException("SSLCheck config undefined");
		this.config = config;
	}

	public int handleEvent(int sequence, CompositeMap context,Object[] parameters) throws Exception {
		MainService svc = MainService.getServiceInstance(context);
		HttpServletRequest request = svc.getRequest();
		String service_name = svc.getServiceName();
		List services = this.config.getAllServices();		
		if (!request.isSecure() && services.contains(service_name.toLowerCase())) {
			String despachUrl = buildSSLUrl(request);
			SessionController sc = SessionController.createSessionController(context);
			sc.setContinueFlag(false);
			sc.setDispatchUrl(despachUrl);
			return EventModel.HANDLE_STOP;
		}
		return EventModel.HANDLE_NORMAL;
	}
	
	public String buildSSLUrl(HttpServletRequest request) throws UnsupportedEncodingException{
		StringBuilder link = new StringBuilder();
		link.append(URL_HTTPS);
        link.append("://");
        link.append(request.getServerName());
        link.append(":");
        link.append(config.getHttpsPort());
        link.append(request.getContextPath());
        link.append(getServletPath(request));
        buildParametersString(request.getParameterMap(), link, "&");
		return link.toString();
	}
	
	
    public static String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        
        String requestUri = request.getRequestURI();
        // Detecting other characters that the servlet container cut off (like anything after ';')
        if (requestUri != null && servletPath != null && !requestUri.endsWith(servletPath)) {
            int pos = requestUri.indexOf(servletPath);
            if (pos > -1) {
                servletPath = requestUri.substring(requestUri.indexOf(servletPath));
            }
        }
        
        if (null != servletPath && !"".equals(servletPath)) {
            return servletPath;
        }
        
        int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath().length();
        int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());

        if (startIndex > endIndex) { // this should not happen
            endIndex = startIndex;
        }

        return requestUri.substring(startIndex, endIndex);
    }
    
    
    public static void buildParametersString(Map params, StringBuilder link, String paramSeparator) throws UnsupportedEncodingException {
        if ((params != null) && (params.size() > 0)) {
            if (link.toString().indexOf("?") == -1) {
                link.append("?");
            } else {
                link.append(paramSeparator);
            }
            Iterator iter = params.entrySet().iterator();


            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();


                if (value instanceof Iterable) {
                    for (Iterator iterator = ((Iterable) value).iterator(); iterator
                        .hasNext();) {
                        Object paramValue = iterator.next();
                        link.append(buildParameterSubstring(name, paramValue
                            .toString()));

                        if (iterator.hasNext())
                            link.append(paramSeparator);
                    }
                } else if (value instanceof Object[]) {
                    Object[] array = (Object[]) value;
                    for (int i = 0; i < array.length; i++) {
                        Object paramValue = array[i];
                        link.append(buildParameterSubstring(name, paramValue
                            .toString()));

                        if (i < array.length - 1)
                            link.append(paramSeparator);
                    }
                } else {
                    link.append(buildParameterSubstring(name, value.toString()));
                }

                if (iter.hasNext())
                    link.append(paramSeparator);
            }
        }
    }
    
    private static String buildParameterSubstring(String name, String value) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append('=');
        builder.append(translateAndEncode(value));

        return builder.toString();
    }
    
    
    public static String translateAndEncode(String input) throws UnsupportedEncodingException {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	throw e;
        }
    }
}
