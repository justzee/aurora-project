/**
 * Created on: 2002-11-13 14:23:32
 * Author:     zhoufan
 */
package org.lwap.ui;

import org.lwap.validation.InputParameter;

/**
 * 
 */
public interface InputField  extends InputParameter{

/** value access */
	public Object getValue();
	
	public void   setValue(Object value);

/** get/set value in String format */	
	public String getStringValue();
	
	public void   setStringValue(String value);

/** Format for display, such as 'YYYY/MM/DD' for date type value */	
	public String getDisplayFormat();
	
	public void   setDisplayFormat(String format);
	
	public int getInputSize();
	
	public void setInputSize( int size);	
	
	public boolean isReadOnly();
	
	public void setReadOnly( boolean read_only);

}
