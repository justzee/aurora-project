/**
 * Created on: 2003-3-11 17:11:27
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.Collection;

import uncertain.composite.CompositeMap;

/**
 * simplely put a list of view together
 */
public class ViewBundle implements View {

	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
			
		Collection childs = view.getChilds();	
		if (childs != null)
				session.applyViews(model,childs);			
	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return "view-bundle";
	}

}
