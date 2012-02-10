/**
 * Created on: 2003-4-21 10:37:46
 * Author:     zhoufan
 */
package org.lwap.application;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletException;

import org.lwap.mvc.ViewCreationException;

import uncertain.composite.CompositeMap;

/**
 * 
 */

public class XMLOutputService extends XMLCheckedService {
	
	/** 用于输出XML内容的model的element名 */
	public static final String KEY_CONTENT = "content";
	
	/** 输出的XML的大小写 */
	public static final String KEY_CASE = "case";
	
	
	public void createView() throws IOException,ServletException
	{
		setViewOutput(true);
	}

	public void buildOutputContent() throws IOException, ViewCreationException, ServletException {
		
		CompositeMap model = getModel();
		if( model  == null ) return;
		
		CompositeMap config = this.getServiceConfig();
		
		String content = config.getString(KEY_CONTENT);
		if( content != null){
			model = model.getChild(content);
			if( model == null) return ;
		}
		response.setContentType("text/xml; charset=\"utf-8\"");
		//response.setLocale(Locale.CHINESE);
		PrintWriter writer = response.getWriter();
		writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		
		String cs = config.getString(KEY_CASE);
		if( cs != null)
			if( "lower".equals(cs)){
				writer.write(model.toXML().toLowerCase());
				return;
			}
		writer.write(model.toXML());
		
	}


}
