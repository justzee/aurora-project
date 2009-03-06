/**
 * Created on: 2002-11-20 13:20:47
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;

import uncertain.composite.CompositeMap;

public abstract class TemplateView implements View  {
	

	
	protected LinkedList content_list;// = new LinkedList();
	
	String		template;	

	
	public String getTemplate(){
		return template;
	}
	
	public void setTemplate(String tp){
		template = tp;
		content_list = TemplateContent.buildTemplateContent(template);
	}
	


	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException 
	{
		if(content_list == null){ 
			//return;
			throw new ViewCreationException("TemplateView: no content built from template");
		}
		Iterator it = content_list.iterator();
		while( it.hasNext())
		try
		{
//			ServletOutputStream stream = session.getOutputStream();
			Writer writer = session.getWriter();
			TemplateContent content = (TemplateContent)it.next();
			if(content.type == TemplateContent.TYPE_TEMPLATE_FRAGMENT)
//				stream.print(content.content);		
			    writer.write(content.content);
			else{
//				stream.print(getTagContent(content.content,session, model,view));	
				String str = getTagContent(content.content,session, model,view);
				if(str != null) writer.write(str);	
			}
		}catch(IOException ex){
			throw new ViewCreationException(ex);
		}
		
	}

  	public abstract String  getTagContent(String tag,	BuildSession session, CompositeMap model, CompositeMap view) throws ViewCreationException;
	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public abstract String getViewName();

}
