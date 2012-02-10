/**
 * Created on: 2002-11-13 14:59:09
 * Author:     zhoufan
 */
package org.lwap.validation;

/**
 * 
 */
public class ValidationException extends Exception {
	
	Object   InputValue;
	String   ErrorType;
	String	 ParameterName;
	Object   AttachedObject;
	String   ParameterPrompt;
	
    /**
     * @return Returns the parameterPrompt.
     */
    public String getParameterPrompt() {
        return ParameterPrompt;
    }
    /**
     * @param parameterPrompt The parameterPrompt to set.
     */
    public void setParameterPrompt(String parameterPrompt) {
        this.ParameterPrompt = parameterPrompt;
    }
	public String getMessage(){
		return getErrorType();
	}
	
	public ValidationException(){
	}

	public ValidationException(String error_type, String parameter_name, Object input_value, Object attached_object){
		setErrorType(error_type);
		setParameterName(parameter_name);
		setInputValue(input_value);
		setAttachedObject(attached_object);
	}


	/**
	 * Returns the attachedObject.
	 * @return Object
	 */
	public Object getAttachedObject() {
		return AttachedObject;
	}

	/**
	 * Returns the errorYype.
	 * @return String
	 */
	public String getErrorType() {
		return ErrorType;
	}

	/**
	 * Returns the inputValue.
	 * @return Object
	 */
	public Object getInputValue() {
		return InputValue;
	}

	/**
	 * Returns the parameterName.
	 * @return String
	 */
	public String getParameterName() {
		return ParameterName;
	}

	/**
	 * Sets the attachedObject.
	 * @param attachedObject The attachedObject to set
	 */
	public void setAttachedObject(Object attachedObject) {
		AttachedObject = attachedObject;
	}

	/**
	 * Sets the errorYype.
	 * @param errorYype The errorYype to set
	 */
	public void setErrorType(String errorType) {
		ErrorType = errorType;
	}

	/**
	 * Sets the inputValue.
	 * @param inputValue The inputValue to set
	 */
	public void setInputValue(Object inputValue) {
		InputValue = inputValue;
	}

	/**
	 * Sets the parameterName.
	 * @param parameterName The parameterName to set
	 */
	public void setParameterName(String parameterName) {
		ParameterName = parameterName;
	}

}