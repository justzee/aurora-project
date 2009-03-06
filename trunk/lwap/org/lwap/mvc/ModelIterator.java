/**
 * Created on: 2004-4-26 20:42:28
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.Collection;
import java.util.Iterator;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class ModelIterator implements View {

	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
		if( model == null) return;
		model = DataBindingConvention.getDataModel(model,view);
		Iterator it = model.getChildIterator();
		Collection childs = view.getChilds();
		if( it == null || childs == null) return;
		while( it.hasNext()){
			CompositeMap item = (CompositeMap)it.next();
			session.applyViews(item, childs);
		}
	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return "iterator";
	}

}
