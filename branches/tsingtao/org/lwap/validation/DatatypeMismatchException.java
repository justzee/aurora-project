/**
 * Created on: 2002-11-14 17:18:32
 * Author:     zhoufan
 */
package org.lwap.validation;


/**
 * 
 */
public class DatatypeMismatchException extends ValidationException {
	
	Class _expected_class;

	/**
	 * Constructor for DatatypeMismatchException.
	 */
	public DatatypeMismatchException(
		Class  expected_class,
		String parameter_name,
		Object input_value,
		Exception parse_exception
		) 
	{
		super(ValidationConstant.ERROR_DATATYPE_MISMATCH + '.' + expected_class.getName(), parameter_name, input_value, parse_exception);
		_expected_class = expected_class;
	}

	/**
	 * Constructor for DatatypeMismatchException.
	 */
	public DatatypeMismatchException(
		Class  expected_class,
		String parameter_name,
		Object input_value,
		Exception parse_exception,
		String parameter_prompt
		) 
	{
		super(ValidationConstant.ERROR_DATATYPE_MISMATCH + '.' + expected_class.getName(), parameter_name, input_value, parse_exception);
		_expected_class = expected_class;
		setParameterPrompt(parameter_prompt);
	}
	
	public Class getExpectedClass(){
		return this._expected_class;
	}

}
