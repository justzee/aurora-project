/** 
 *  Hold configuration to connect to SAP server
 *  Created on 2006-6-14
 */
package org.lwap.sapplugin;

import java.util.logging.Logger;

import uncertain.core.IGlobalInstance;

public class SapInstance extends InstanceConfig{
	public SapInstance() {
		super();		
	}
	
	public SapInstance(Logger logger) {
		super(logger);		
	}
	
}
