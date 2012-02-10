/**
 * Created on: 2002-11-15 22:10:52
 * Author:     zhoufan
 */
package org.lwap.application;

import uncertain.composite.CompositeMap;

public interface Application {

	public CompositeMap getApplicationConfig();
	
	public Service getService(String service_name) throws ServiceInstantiationException;
	
    public void shutdown();
	
}
