/**
 * Created on: 2002-12-3 17:27:03
 * Author:     zhoufan
 */
package org.lwap.ui;

import uncertain.composite.CompositeMap;

/** static methods for creating instance of UI component
 *  
 */
public class UIFactory {
	
	public static InputField createInputField( CompositeMap field_config){
		return InputFieldImpl.createInputField(field_config);		
	}
	
	public static SelectiveField createSelectiveField(CompositeMap field_config){	
		return SelectiveFieldImpl.createSelectiveField(field_config);
	}
	
	public static UIComponent createUIComponent(CompositeMap view_config){
		UIComponent comp = new UIComponent();
		comp.initialize(view_config);
		return comp;
	}
	

}
