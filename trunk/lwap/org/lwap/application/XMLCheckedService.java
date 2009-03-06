/**
 * Created on: 2003-11-24 15:23:05
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;

import javax.servlet.ServletException;

import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class XMLCheckedService extends BaseService {

	public static final String DEFAULT_ACCESS_CHECK = "WebReportCheck.data";
	public static final String KEY_USER_NAME = "UserName";
	public static final String KEY_USER_PWD = "UserPassword";
	public static final String KEY_SERVICE_NAME = "service-name";	

	/**
	 * @see org.lwap.application.BaseService#preService()
	 */
	public void preService() throws IOException, ServletException {
		super.preService();

		String url = request.getRequestURI();
		String svc_name = url.substring( url.lastIndexOf('/') +1 , url.lastIndexOf('.'));
		getServiceContext().put(KEY_SERVICE_NAME, svc_name);
		
		CompositeMap params = getParameters();
		params.put(KEY_USER_NAME, request.getParameter(KEY_USER_NAME));
		params.put(KEY_USER_PWD, request.getParameter(KEY_USER_PWD));
	}
	

	/**
	 * @see org.lwap.application.BaseService#checkPrivilege()
	 */
	public void checkPrivilege() throws IOException, NoPrivilegeException {
		boolean access_check = super.getServiceConfig().getBoolean("access-check", true);
		if( !access_check) return;
		CompositeMap ac = getServiceConfigSection(BaseService.KEY_ACCESS_CHECK);
		if( ac == null) 
		try{
			getServiceConfig().addChild(application.getCompositeLoader().load(DEFAULT_ACCESS_CHECK));
			//System.out.println(getServiceConfig().toXML());
		} catch( SAXException ex){
			ex.printStackTrace();			
		}
		super.checkPrivilege();
	}
}
