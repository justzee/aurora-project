/**
 * Created on: 2002-11-18 16:14:19
 * Author:     zhoufan
 */
package org.lwap.ui;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import uncertain.composite.CompositeMap;

public class HtmlOutput {
	

	public static void putAttribute(JspWriter out, CompositeMap map, String attrib_name)
		throws IOException
	{
		putAttribute(out,map,attrib_name,null);
	}

	public static void putAttribute(JspWriter out, CompositeMap map, String attrib_name, String default_value)
		throws IOException
	{
		putAttribute( out, attrib_name, map.getString(attrib_name),default_value);	
	}
	
	public static void putAttribute(JspWriter out, String attrib_name, String value)
		throws IOException
	{
		putAttribute(out,attrib_name,value, null);
	}
	
	
	public static void putAttribute(JspWriter out, String attrib_name, String value, String default_value)
		throws IOException
	{
		String vl = (value==null?default_value:value);
		if( vl == null) return;
		out.print(attrib_name);
		out.print("=\"");
		out.print(vl);
		out.print('\"');
	}
/*	
	public static void main(String[] args) throws Exception{
		System.out.println("test");
		putHtmlAttribute(System.out, "Width", "15",null);
	}
	*/

}
