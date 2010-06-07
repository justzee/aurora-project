/**
 * Created on: 2002-11-20 19:25:03
 * Author:     zhoufan
 */
package org.lwap.mvc;

import uncertain.composite.CompositeMap;
/**
 * 
 */
public interface SelectionDecider {
	
	public void make( CompositeMap map, CompositeMap view);
	
}
