/**
 * Created on: 2002-11-19 20:17:15
 * Author:     zhoufan
 */
package org.lwap.mvc;

import uncertain.composite.CompositeMap;

public interface View {
	
	public void build(BuildSession session, CompositeMap model, CompositeMap view ) throws ViewCreationException;
	
	public String getViewName();
	
}
