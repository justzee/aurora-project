/**
 * Created on: 2002-11-28 12:40:50
 * Author:     zhoufan
 */
package org.lwap.ui;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.validation.InputParameterImpl;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class InputFieldImpl extends InputParameterImpl implements InputField, DataBinding {	
	
	CompositeMap		model;
	
	
	public static InputField createInputField( CompositeMap view_config){
		InputFieldImpl fld = new InputFieldImpl();
		fld.initialize(view_config);
		return fld;
	}

	/**
	 * @see org.lwap.ui.InputField#getValue()
	 */
	public Object getValue() {
		
		Object vl = getObjectContext().get(DataBindingConvention.KEY_DATAVALUE);
		
		
		if( vl == null) {
			if( getBindedModel() != null)
				return getBindedModel().getObject( getObjectContext().getString(DataBindingConvention.KEY_DATAFIELD) );
			else
				return null;	
		}

		Class data_type = getDataTypeClass();
		if( data_type == null) return vl;
		
		if( data_type.equals(String.class)) return vl.toString();

		if( vl instanceof String)
		try{
//			Object obj = super.parseString((String)vl);
			Object obj = DataTypeManager.parseObject(data_type,(String)vl);
			setValue( obj);
			return obj;
		} catch(Exception ex){
			return vl;
		}
		
		else
			return vl;
	}

	/**
	 * @see org.lwap.ui.InputField#setValue(Object)
	 */
	public void setValue(Object value) {
		getObjectContext().put( DataBindingConvention.KEY_DATAVALUE,value);		
	}

	/**
	 * @see org.lwap.ui.InputField#getStringValue()
	 */
	public String getStringValue() {
/*
		Object obj = getValue();
		return obj==null?null:obj.toString();
*/
		return getString(DataBindingConvention.KEY_DATAVALUE);
		
	}

	/**
	 * @see org.lwap.ui.InputField#setStringValue(String)
	 */
	public void setStringValue(String value) {
		setValue(value);
	}


	/**
	 * @see org.lwap.ui.InputField#getDisplayFormat()
	 */
	public String getDisplayFormat() {
		return getString(UIAttribute.ATTRIB_DISPLAY_FORMAT);
	}

	/**
	 * @see org.lwap.ui.InputField#setDisplayFormat(String)
	 */
	public void setDisplayFormat(String format) {
		putString(UIAttribute.ATTRIB_DISPLAY_FORMAT,format);
	}
	

	public int getInputSize(){
		return getInt(UIAttribute.ATTRIB_INPUT_SIZE,20);
	}
	
	public void setInputSize( int size){
		putInt(UIAttribute.ATTRIB_INPUT_SIZE,size);		
	}
	
	public String getDataField(){
		return getString( DataBindingConvention.KEY_DATAFIELD);
	}
	
	public void setDataField(String field){
		putString(DataBindingConvention.KEY_DATAFIELD, field);
	}
	
	public String getDataModel(){
		return getString( DataBindingConvention.KEY_DATAMODEL);
	}
	
	public void setDataModel(String model){
		putString(DataBindingConvention.KEY_DATAMODEL, model);
	}
	
	public void bindModel( CompositeMap model){
		this.model = DataBindingConvention.getDataModel( model, this.getObjectContext());
	}

	public CompositeMap getBindedModel(){
		return this.model;	
	}
	
	public boolean isReadOnly(){
		return getBoolean( UIAttribute.ATTRIB_READ_ONLY, false);
	}
	
	public void setReadOnly( boolean read_only){
		putBoolean(  UIAttribute.ATTRIB_READ_ONLY, read_only);	
	}
	

	public static void main(String[] args) throws Exception {
		CompositeMap fld = new CompositeMap("parameter");
		fld.put("Name", "test");
		fld.put("DataType", "java.lang.Long");
		fld.put(DataBindingConvention.KEY_DATAVALUE, "1200000");
		
		InputField f = createInputField(fld);
		System.out.println(f.getValue().getClass());
	}
}
