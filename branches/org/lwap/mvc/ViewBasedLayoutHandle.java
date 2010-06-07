/**
 * Created on: 2002-12-30 15:22:24
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.Iterator;

import uncertain.composite.CompositeMap;

public class ViewBasedLayoutHandle extends HtmlLayoutHandle {
	
	protected Iterator view_iterator;
	
	/**
	 * @see org.lwap.mvc.HtmlLayoutHandle#createCellContent()
	 */
	public void createCellContent() throws ViewCreationException {
		CompositeMap child_view = (CompositeMap) view_iterator.next();	
/*		try{
		session.getWriter().flush();
		}catch(IOException ex){
		    
		}*/
		session.buildView(super.origin_model,child_view);
	}

	/**
	 * @see org.lwap.mvc.TabularLayout.LayoutHandle#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		if( view_iterator == null)
			return false;
		else
			return view_iterator.hasNext();	
	}

	/**
	 * @see org.lwap.mvc.TabularLayout.LayoutHandle#onLayoutBegin(BuildSession, CompositeMap, CompositeMap)
	 */
	public void onLayoutBegin(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
		super.onLayoutBegin(session, model, view);
		view_iterator = view.getChildIterator();			
	}

};	
