/**
 * Created on: 2003-5-16 9:49:34
 * Author:     zhoufan
 */
package org.lwap.application;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public interface ExceptionHandle {
	
//	public void init( WebApplication app) ;
	
	public void handleException(
		Throwable			thr,
		WebApplication		app,
		Service				service,
		CompositeMap 		config 
		);

}
