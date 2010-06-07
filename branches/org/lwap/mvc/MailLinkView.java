/**
 * Created on: 2002-11-20 19:06:15
 * Author:     zhoufan
 */
package org.lwap.mvc;

import uncertain.composite.CompositeMap;

public class MailLinkView extends LinkView {

	protected String getTemplateName(){
		return  LinkView.class.getName();
	}
	

	public String getViewName(){
		return "maillink";
	}
	
	public String getHRefContent(BuildSession session, CompositeMap model, CompositeMap view ) throws ViewCreationException{
		return "mailto:" + super.getHRefContent(session,model,view);
	}	
			

}
