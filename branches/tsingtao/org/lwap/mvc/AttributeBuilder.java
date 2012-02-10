/**
 * Created on: 2002-12-30 11:27:06
 * Author:     zhoufan
 */
package org.lwap.mvc;

import uncertain.composite.CompositeMap;

/**
 * Utility class to build XML style attribute declaration
 */
public class AttributeBuilder {

	public static void createAttrib( StringBuffer buf, CompositeMap source,  String attrib_name, String value_key){
		Object obj = source.get(value_key);
		if( obj != null){
			buf.append(' ').append(attrib_name)
			   .append("=\"").append(obj.toString()).append('\"');
		}
	}
	
	public static void createAttrib( StringBuffer buf, CompositeMap source, String attrib_name){
		createAttrib(buf,source,attrib_name,attrib_name);
	}

}
