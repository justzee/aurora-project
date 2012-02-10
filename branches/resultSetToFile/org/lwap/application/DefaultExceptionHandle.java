/**
 * Created on: 2003-5-16 14:06:43
 * Author:     zhoufan
 */
package org.lwap.application;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class DefaultExceptionHandle implements ExceptionHandle {
	
	public static final String KEY_PROMPT = "prompt";
	public static final String KEY_LOG = "log";

	/**
	 * @see org.lwap.application.ExceptionHandle#handleException(Throwable, WebApplication, Service, CompositeMap)
	 */
	public void handleException(
		Throwable thr,
		WebApplication app,
		Service service,
		CompositeMap config) {
			
			boolean log_exception = true;
			if( config != null){
				config.getBoolean(KEY_LOG, true);
			}
	}

}
