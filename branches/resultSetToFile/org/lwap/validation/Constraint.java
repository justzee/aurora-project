/**
 * Created on: 2002-11-13 14:57:33
 * Author:     zhoufan
 */
package org.lwap.validation;

/**
 * 
 */
public interface Constraint {
	
	public void enforce( Object input_value) throws ValidationException;

}
