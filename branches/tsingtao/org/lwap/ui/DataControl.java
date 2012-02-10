/**
 * Created on: 2003-12-30 13:07:17
 * Author:     zhoufan
 */
package org.lwap.ui;

import org.lwap.mvc.DataBindingConvention;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * 
 */
public class DataControl extends DynamicObject {
	
	protected CompositeMap	model;
	
	public void bindModel( CompositeMap m ){
		model = DataBindingConvention.getDataModel(m,getObjectContext());		
	}

}
