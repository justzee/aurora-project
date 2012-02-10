/**
 * Created on: 2002-11-13 14:43:02
 * Author:     zhoufan
 */
package org.lwap.validation;

import java.util.Collection;

/**
 *  Default property for parameter inputs from client side
 */
public interface InputParameter {
    
	public static final String KEY_ParameterName = "Name";
	public static final String KEY_DataType = "DataType";
	public static final String KEY_Nullable = "Nullable";
	public static final String KEY_Constraints = "Constraints";
	public static final String KEY_DefaultValue = "DefaultValue";
	public static final String KEY_InputFormat = "InputFormat";	

/** name of parameter */	
	public String getParameterName();
	
	public void setParameterName(String name);

/** data type, usually in java class name */
	public String getDataType();
	
	public void  setDataType(String data_type);
	
	public Class getDataTypeClass();	
	
	public void setDataTypeClass( Class datatype_class );
	
/** validation constraint */
	public boolean getNullable();
	
	public void setNullable( boolean nullable);

/*
	public int getMaxLength();
	
	public void setMaxLength( int length);
	
	public int getMinLength();

	public void setMinLength( int length);
	*/
	
	public Collection getConstraints();
	
	public void setConstraints( Collection constraint);


/** default value */	
	public Object getDefaultValue();
	
	public void setDefaultValue(Object value);
	

/** Format for input value, such as 'YYYY/MM/DD' for date type value */	
	public String getInputFormat();
	
	public void   setInputFormat(String format);
	
	public Object parseString( String input) throws ValidationException ;	
	
}
