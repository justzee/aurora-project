/**
 * Created on: 2002-11-18 16:06:18
 * Author:     zhoufan
 */
package org.lwap.ui.web;

import java.util.Iterator;

import org.lwap.mvc.DataBindingConvention;
import org.lwap.ui.UIAttribute;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class Table extends DynamicObject {
	
	public static final String KEY_HEAD_ALIGN = "HeadAlign";
	
	public static String getHeadAlign(CompositeMap map){
		return map.getString(KEY_HEAD_ALIGN, UIAttribute.getAlign(map));
	}
	
	public static String getAlign(CompositeMap map){
		return UIAttribute.getAlign(map);
	}
	
	
	public void initTable(){
		String entity = getObjectContext().getString(DataBindingConvention.KEY_ENTITY);
		Iterator columns = getObjectContext().getChildIterator();
		if( columns == null) return;
		while( columns.hasNext()){
			CompositeMap column = (CompositeMap)columns.next();
			String name = UIAttribute.getName(column);
			if( name == null) continue;
			// set data field
			if( !column.containsKey(DataBindingConvention.KEY_DATAFIELD))
				column.put(DataBindingConvention.KEY_DATAFIELD, '@' + name);
			// set default prompt	
			if( !column.containsKey(UIAttribute.ATTRIB_PROMPT) && entity != null)
				column.put(UIAttribute.ATTRIB_PROMPT, entity + '.' + name);			
		}
//		System.out.println(getObjectContext().toXML());
	}
	

	public static void main(String[] args){
		CompositeMap map = new CompositeMap();
		map.put(UIAttribute.ATTRIB_ALIGN,"right");
		System.out.println( getHeadAlign(map));
		map.put(KEY_HEAD_ALIGN, "center");
		System.out.println( getHeadAlign(map));
	}
}
