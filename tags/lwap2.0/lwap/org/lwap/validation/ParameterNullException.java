/**
 * Created on: 2002-11-14 21:14:08
 * Author:     zhoufan
 */
package org.lwap.validation;

/**
 * 
 */
public class ParameterNullException extends ValidationException {
    
	/**
	 * Constructor for ParameterNullException.
	 * @param error_type
	 * @param parameter_name
	 * @param input_value
	 * @param attached_object
	 */
	public ParameterNullException(	String parameter_name) {
		super( ValidationConstant.ERROR_NULL , parameter_name, null, null);
	}
	
	public ParameterNullException(	String parameter_name, String parameter_prompt) {
		super( ValidationConstant.ERROR_NULL , parameter_name, null, null);
		setParameterPrompt(parameter_prompt);
	}
	

    public String getMessage() {       
        return "Required parameter "+getParameterName()+" is null";
    }
}
