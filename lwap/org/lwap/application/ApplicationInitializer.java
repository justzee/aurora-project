/**
 * Created on: 2002-11-15 22:14:52
 * Author:     zhoufan
 */
package org.lwap.application;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public interface ApplicationInitializer {
	
	public void initApplication( Application app, CompositeMap app_config) throws ApplicationInitializeException;
	
	public void cleanUp(Application app);

}
