/**
 * Created on: 2003-3-4 9:14:34
 * Author:     zhoufan
 */
package org.lwap.mvc;

import uncertain.composite.CompositeMap;

/**
 *  Populate view before compose with model
 */

public interface ViewPopulate {
	
	public void populateView( CompositeMap model, CompositeMap view)
		throws ViewCreationException;	

}
