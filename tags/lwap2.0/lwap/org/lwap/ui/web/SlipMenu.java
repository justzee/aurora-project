/**
 * Created on: 2002-11-29 17:07:43
 * Author:     zhoufan
 */
package org.lwap.ui.web;

import org.lwap.ui.UIComponent;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class SlipMenu extends UIComponent {
	
	public static SlipMenu createMenu(CompositeMap view){
		SlipMenu menu = new SlipMenu();
		menu.initialize(view);
		return menu;
	}

	public static final String KEY_HEADHEIGHT= "HeadHeight";
	public static final String KEY_BODYHEIGHT= "BodyHeight";
	public static final String KEY_SLIP_SPEED = "SlipSpeed";
/*
	public static final String KEY_= "";
	public static final String KEY_= "";
	public static final String KEY_= "";
	public static final String KEY_= "";
*/
	
	public void setHeadHeight( int height){
		putInt( KEY_HEADHEIGHT, height);
	}	
	
	public int getHeadHeight(){
		return getInt(KEY_HEADHEIGHT, 0);
	}
	
	public void setBodyHeight( int height){
		putInt( KEY_BODYHEIGHT, height);
	}
	
	public int getBodyHeight(){
		return getInt( KEY_BODYHEIGHT, 0);
	}

	public void setSlipSpeed(int spd){
		putInt(KEY_SLIP_SPEED,spd);
	}
	
	public int getSlipSpeed(){
		return getInt(KEY_SLIP_SPEED,10);
	}


}
