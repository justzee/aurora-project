/**
 * Created on: 2002-12-3 18:43:53
 * Author:     zhoufan
 */
package org.lwap.ui.web;

import org.lwap.ui.SelectiveFieldImpl;
import org.lwap.ui.UIAttribute;

/**
 * 
 */
public class RadioGroup extends SelectiveFieldImpl {
	
	public int getOrientation(){
		return UIAttribute.VALUE_NAME_HORIZONTAL.equals( getString(UIAttribute.ATTRIB_ORIENTATION) )? UIAttribute.VALUE_HORIZONTAL: UIAttribute.VALUE_VERTICAL;
	}

}
