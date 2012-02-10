/**
 * Created on: 2002-11-14 16:08:14
 * Author:     zhoufan
 */
package org.lwap.validation;

import java.util.Collection;
import java.util.Map;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * 
 */
public class InputParameterImpl extends DynamicObject implements InputParameter {

	public static CompositeMap createParameter( String name, Class data_type, boolean nullable, Object default_value){
		InputParameterImpl param = new InputParameterImpl();
		param.initialize();
		param.setParameterName(name);
		param.setDataType(data_type.getName());
		param.setNullable(nullable);
		param.setDefaultValue(default_value);
		return (CompositeMap)param.getObjectContext();
	}
	
	public static InputParameter createInputParameter( CompositeMap param_config){
		InputParameterImpl param = new InputParameterImpl();
		param.initialize(param_config);
		return param;
	}

	public String getParameterName() {
		return getString( InputParameter.KEY_ParameterName);
	}

	/**
	 * @see org.lwap.validation.InputParameter#setParameterName(String)
	 */
	public void setParameterName(String name) {
		putString(InputParameter.KEY_ParameterName, name);
	}

	/**
	 * @see org.lwap.validation.InputParameter#getDataType()
	 */
	public String getDataType() {
		return getString(InputParameter.KEY_DataType, "java.lang.String");
	}
	
	
	
	/**
	 * return datatype of parameter in lava.lang.Class.
	 * If "DataType" attribute is a String, A new Class instance will be parsed
	 * from attribute value and put back to object context.
	 * @return java.lang.Class
	 */
	public Class getDataTypeClass(){
		Object obj = getObjectContext().get(KEY_DataType);
		
		if( obj == null){ 
			Class cls = String.class;
			setDataTypeClass(cls);
			return cls;
		}
		
		if( obj instanceof Class) return (Class)obj;
		else if( obj instanceof String){
            String data_type = getDataType();            
			Class cls =  DataTypeManager.getClassByName( data_type );
			if( cls == null){
                try{
                    Class.forName(data_type);
                }catch(ClassNotFoundException ex){
                    throw new IllegalArgumentException("Class not found:"+data_type);
                }
                cls = DataTypeManager.getClassByName( data_type );
            }
            if( cls == null) return null;
			setDataTypeClass(cls);
			return cls;			
		}
		else return null;
		
	}
	
	public void setDataTypeClass( Class datatype_class ){
		getObjectContext().put(KEY_DataType,datatype_class);
	}

	/**
	 * @see org.lwap.validation.InputParameter#setDataType(String)
	 */
	public void setDataType(String data_type) {
		putString(InputParameter.KEY_DataType, data_type);
	}

	/**
	 * @see org.lwap.validation.InputParameter#getNullable()
	 */
	public boolean getNullable() {
		return getBoolean( InputParameter.KEY_Nullable, true);		
	}

	/**
	 * @see org.lwap.validation.InputParameter#setNullable(boolean)
	 */
	public void setNullable(boolean nullable) {
		putBoolean( InputParameter.KEY_Nullable,nullable); 
	}

	CompositeMap getConstraintSection(){
		Map context = getObjectContext();
		if( context instanceof CompositeMap)
			return ((CompositeMap)context).getChild(InputParameter.KEY_Constraints);
		else return null;
		
	}

	/**
	 * @see org.lwap.validation.InputParameter#getConstraints()
	 */
	public Collection getConstraints() {
		CompositeMap constraints = getConstraintSection();
		return constraints == null ? null: constraints.getChilds();
	}

	/**
	 * @see org.lwap.validation.InputParameter#setConstraints(Collection)
	 */
	public void setConstraints(Collection constraint) {
		CompositeMap constraints = getConstraintSection();
		if( constraints != null) constraints.addChilds(constraint);
	}

	/**
	 * @see org.lwap.validation.InputParameter#getDefaultValue()
	 */
	public Object getDefaultValue() {
		return getObjectContext().get(InputParameter.KEY_DefaultValue);
	}

	/**
	 * @see org.lwap.validation.InputParameter#setDefaultValue(Object)
	 */
	public void setDefaultValue(Object value) {
		getObjectContext().put(InputParameter.KEY_DefaultValue,value);
	}

	/**
	 * @see org.lwap.validation.InputParameter#getInputFormat()
	 */
	public String getInputFormat() {
		return getString(InputParameter.KEY_InputFormat);
	}

	/**
	 * @see org.lwap.validation.InputParameter#setInputFormat(String)
	 */
	public void setInputFormat(String format) {
		putString(InputParameter.KEY_InputFormat, format);
	}
	

	public Object parseString( String input) throws ValidationException {
	    String prompt = this.getObjectContext().getString("Prompt"); 
	        
		Class cls = this.getDataTypeClass();
		if( cls == null)	throw new IllegalArgumentException("Unknown data type:"+getDataType());

		DatabaseTypeField fld =  DataTypeManager.getType(cls);
		if( fld == null) {
			throw new IllegalArgumentException("Unknown data type:"+cls);
		}

		if( input == null){

			Object dv = this.getDefaultValue();
			if( dv == null){
				if( this.getNullable())
					return null;
				else
					throw new ParameterNullException( this.getParameterName(), prompt);
			}
			else{	
				if( dv instanceof String && ! this.getDataTypeClass().equals(String.class)) 
					return parseString( (String)dv);
				else
					return dv;
			}	
		}
		
		try{
			return fld.parseObject(input);
		}catch(Exception ex){
			throw new DatatypeMismatchException( fld.getFieldClass(),this.getParameterName(),input,ex, prompt);
		}
		
	}	

	/**
	 * @see org.lwap.composite.DynamicObject#initialize(Map)
	 */
/*
	public DynamicObject initialize(CompositeMap context) throws ClassCastException {
		super.initialize(context);
		if( getParameterName() == null) throw new ClassCastException("no Name specified for InputParameter");
		return this;
	}
*/	
	
	/**
	 * @see uncertain.composite.DynamicObject#initialize()
	 */
	
	public DynamicObject initialize() {
		super.initialize();
		((CompositeMap)getObjectContext()).setName("param");
		return this;
	}
	
	
	public static void main(String[] args) throws Exception{

		CompositeMap param =InputParameterImpl.createParameter("pagenum",Long.class,true,new Long(30));
//		System.out.println(param.toXML());

		InputParameter pm = createInputParameter(param);
		
		System.out.println("parsed value:"+pm.parseString("100000").getClass());
		
		pm.setDataType("java.lang.Boolean");
		System.out.println("new value:"+pm.parseString("false"));
		
		pm.setDataTypeClass(String.class);
		System.out.println("new value:"+pm.parseString("Hello,world"));
		
		
	}


}
