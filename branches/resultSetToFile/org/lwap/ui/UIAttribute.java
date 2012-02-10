/**
 * Created on: 2002-11-13 13:46:58
 * Author:     zhoufan
 */
package org.lwap.ui;

import uncertain.composite.CompositeMap;

/**
 *  Constant define for UI attributes
 */
public class UIAttribute {

/*	
	public static final String ATTRIB_				= "valign";
*/	

/** basic component attributes */
	public static final String ATTRIB_NAME				= "Name";


/** component location */
	public static final String ATTRIB_WIDTH 				= "Width";
	public static final String ATTRIB_HEIGHT 			= "Height";
	public static final String ATTRIB_LEFT 				= "Left";
	public static final String ATTRIB_TOP 				= "Top";
	public static final String ATTRIB_ALIGN				= "Align";
	public static final String ATTRIB_VALIGN				= "Valign";
	
	public static final String ATTRIB_ORIENTATION		= "Orientation";
	public static final String VALUE_NAME_HORIZONTAL  		= "horizontal";
	public static final String VALUE_NAME_VERTICAL  		= "vertical";
	public static final int    VALUE_HORIZONTAL  		= 0;
	public static final int    VALUE_VERTICAL  		= 1;

/** prompt & hint */	
	public static final String ATTRIB_PROMPT 			= "Prompt";
	public static final String ATTRIB_HINT 				= "Hint";
	public static final String ATTRIB_HELP_MESSAGE		= "HelpMessage";

/** Attributes for input field */
	public static final String ATTRIB_DISPLAY_FORMAT = "DisplayFormat";
	public static final String ATTRIB_INPUT_FORMAT = "InputFormat";
	public static final String ATTRIB_INPUT_SIZE = "InputSize";
	public static final String ATTRIB_READ_ONLY = "ReadOnly";	
	
/** Visual Attribute*/	
	public static final String ATTRIB_STYLE_CLASS = "StyleClass";
	public static final String ATTRIB_COLOR = "Color";
	public static final String ATTRIB_BACKGROUD_COLOR = "BackgroundColor";
//	public static final String ATTRIB_	
	
	public static String getAlign(CompositeMap map){
		return map.getString(ATTRIB_ALIGN);
	}
	
	public static String getValign(CompositeMap map){
		return map.getString(ATTRIB_VALIGN);		
	}
	
	public static String getWidth(CompositeMap map){
		return map.getString(ATTRIB_WIDTH);		
	}
	
	public static String getHeight(CompositeMap map){
		return map.getString(ATTRIB_HEIGHT);		
	}	

	public static String getName( CompositeMap map){
		return map.getString(ATTRIB_NAME);
	}

	public static String getPrompt( CompositeMap map){
		return map.getString(ATTRIB_PROMPT);
	}
	
	public static String getPromptForDisplay( CompositeMap map){
		return map.getString(ATTRIB_PROMPT, "");		
	}
	

}

