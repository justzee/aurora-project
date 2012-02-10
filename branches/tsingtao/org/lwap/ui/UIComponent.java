/**
 * Created on: 2002-11-29 17:11:03
 * Author:     zhoufan
 */
package org.lwap.ui;

import uncertain.composite.DynamicObject;

/**
 * 
 */
public class UIComponent
	extends DynamicObject
	implements Region, NamedComponent, VisualControl {

	/**
	 * @see org.lwap.ui.Region#getLeft()
	 */
	public int getLeft() {
		return getInt( UIAttribute.ATTRIB_LEFT, 0) ;
	}

	/**
	 * @see org.lwap.ui.Region#getTop()
	 */
	public int getTop() {
		return getInt( UIAttribute.ATTRIB_TOP, 0) ;		
	}

	/**
	 * @see org.lwap.ui.Region#getWidth()
	 */
	public int getWidth() {
		return getInt( UIAttribute.ATTRIB_WIDTH, 100) ;
	}

	/**
	 * @see org.lwap.ui.Region#getHeight()
	 */
	public int getHeight() {
		return getInt( UIAttribute.ATTRIB_HEIGHT, 100) ;
	}
	
	public int getLeft(int default_value){
		return getInt(UIAttribute.ATTRIB_LEFT,default_value);
	}

	public int getTop(int default_value) {
		return getInt( UIAttribute.ATTRIB_TOP, default_value) ;		
	}

	public int getWidth(int default_value) {
		return getInt( UIAttribute.ATTRIB_WIDTH, default_value) ;
	}

	public int getHeight(int default_value) {
		return getInt( UIAttribute.ATTRIB_HEIGHT, default_value) ;
	}

	/**
	 * @see org.lwap.ui.Region#setLeft()
	 */
	public void setLeft(int left) {
		putInt(UIAttribute.ATTRIB_LEFT, left);
	}

	/**
	 * @see org.lwap.ui.Region#setTop()
	 */
	public void setTop(int top) {
		putInt(UIAttribute.ATTRIB_TOP, top);
	}

	/**
	 * @see org.lwap.ui.Region#setWidth()
	 */
	public void setWidth(int width) {
		putInt(UIAttribute.ATTRIB_WIDTH, width);
	}

	/**
	 * @see org.lwap.ui.Region#setHeight()
	 */
	public void setHeight(int height) {
		putInt(UIAttribute.ATTRIB_HEIGHT, height);		
	}

	/**
	 * @see org.lwap.ui.NamedComponent#getComponentName()
	 */
	public String getComponentName() {
		return getString( UIAttribute.ATTRIB_NAME);
	}

	/**
	 * @see org.lwap.ui.NamedComponent#setComponentName(String)
	 */
	public void setComponentName(String name) {
		putString(UIAttribute.ATTRIB_NAME,name);
	}
	
	
	public String getStyleClass(){
		return getString( UIAttribute.ATTRIB_STYLE_CLASS);
	}
	
	public String getColor(){
		return getString( UIAttribute.ATTRIB_COLOR);
	}
	
	public String getBackgroundColor(){
		return getString( UIAttribute.ATTRIB_BACKGROUD_COLOR);
	}

	

}
