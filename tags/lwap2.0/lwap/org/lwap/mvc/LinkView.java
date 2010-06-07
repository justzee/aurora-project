/**
 * Created on: 2002-11-19 20:37:25
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.lwap.ui.UIAttribute;

import uncertain.composite.CompositeMap;

/** Creates an HTML link 
 *  View: <link *HRef="url" Target="target" *Title="title" *Prompt="prompt"/>
 *        attributes marked with '*' may contain model access tags such as ${employee/@EMPLOYEE_ID}
 *  output:<a href="${HRef}" target="${Target}" title="${Title}">${LinkPrompt}</a>	
 * 
 */
   

public class LinkView implements View {
	
//	public static final String TEMPLATE_NAME =	"LinkView";
	public static final String KEY_HREF   = "HRef";
	public static final String KEY_TARGET = "Target";
	public static final String KEY_CONTENT  = "Content";
	
	
	static final String VIEW_NAME = "link";
	
	public String getViewName(){
		return VIEW_NAME;
	}
	
	public String getHRefContent(BuildSession session, CompositeMap model, CompositeMap view ) throws ViewCreationException{
		return DataBindingConvention.parseAttribute(KEY_HREF,model,view);	
	}
	
	public void build(BuildSession session, CompositeMap model, CompositeMap view ) throws ViewCreationException{
		try{
			Writer out = session.getWriter();
			out.write("<a href=\"" + getHRefContent(session, model, view) + '\"');
			if( view.keySet().contains(KEY_TARGET)) 
				out.write(" target=\"" + view.getString(KEY_TARGET) + '\"');
			if( view.keySet().contains(UIAttribute.ATTRIB_STYLE_CLASS))
				out.write(" class=\"" + view.getString(UIAttribute.ATTRIB_STYLE_CLASS) + '\"' );
			if( view.keySet().contains(UIAttribute.ATTRIB_PROMPT))
				out.write(" title=\"" + DataBindingConvention.parseAttribute(UIAttribute.ATTRIB_PROMPT, model, view) + '\"' );			
			if( view.keySet().contains("onClick"))
				out.write(" onclick=\"" + DataBindingConvention.parseAttribute("onClick", model, view) + '\"' );			

			out.write(">");
			createLinkContent(session, model, view);
			out.write("</a>");
			out.flush();
		} catch(IOException ex){
		}
		
	}
	
	public void createLinkContent(BuildSession session, CompositeMap model, CompositeMap view ) throws ViewCreationException{
		if( view.getChilds() != null){
			session.applyViews(model,view.getChilds());
		} else {
			PrintWriter out = session.getPrintWriter();
			out.write(DataBindingConvention.parseAttribute(KEY_CONTENT,model,view));
		}
	}
	
	
	
/*	
	public String getHRef(BuildSession session, CompositeMap model, CompositeMap view){
		return DataBindingConvention.parseAttribute(KEY_HREF, model, view);		
		
	}
	
	public String getTarget(BuildSession session,CompositeMap model, CompositeMap view){
		return view.getString(KEY_TARGET,"_self");
	}
	
	public String getPrompt(BuildSession session,CompositeMap model, CompositeMap view){
		return DataBindingConvention.parseAttribute(UIAttribute.ATTRIB_PROMPT, model, view);		
	}
	
	public String getContent(BuildSession session,CompositeMap model, CompositeMap view){		
		return DataBindingConvention.parseAttribute(KEY_CONTENT, model, view);
	}
	
	public String getStyleClass(BuildSession session,CompositeMap model, CompositeMap view){		
		return view.getString(UIAttribute.ATTRIB_STYLE_CLASS,"");
	}
	
	public void dump(){
		System.out.println(super.content_list);
	}
	
*/	



	

}
