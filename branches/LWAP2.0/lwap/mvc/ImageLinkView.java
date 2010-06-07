/**
 * Created on: 2002-11-28 14:23:22
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.IOException;
import java.io.Writer;

import org.lwap.ui.UIAttribute;

import uncertain.composite.CompositeMap;

public class ImageLinkView extends LinkView {
	
	public static final String KEY_IMAGE = "Image";

	protected String getTemplateName(){
		return  LinkView.class.getName();
	}
	

	public String getViewName(){
		return "imagelink";
	}
	
	public void createLinkContent(BuildSession session,CompositeMap model, CompositeMap view){
		
		StringBuffer buf = new StringBuffer();
		String image = DataBindingConvention.parseAttribute(KEY_IMAGE, model, view);
		String prompt = DataBindingConvention.parseAttribute(UIAttribute.ATTRIB_PROMPT, model, view);
		
		buf.append("<img src=\"");
		buf.append(image);
		buf.append("\" ");
		if( prompt != null){
			buf.append("alt=\"");
			buf.append(prompt);
			buf.append("\" ");
		}
		buf.append("border=\"0\" ></img>");
		
		Writer writer = session.getWriter();
		try{
			writer.write(buf.toString());
		} catch(IOException ex){
		}
	}	
	

}
