/**
 * Created on: 2002-12-2 11:06:19
 * Author:     zhoufan
 */
package org.lwap.ui;

import java.util.Collection;
import java.util.Iterator;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.mvc.DataBindingConvention;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/** 
 * 
 */
public class SelectiveFieldImpl extends InputFieldImpl implements SelectiveField {
	
	CompositeMap	binded_datasource;
	
	public class PromptGetter implements SelectiveField.SelectOptionProcessor{
		
		String prompt;
		
    	public boolean processOption( Object value, String prompt, boolean is_selected){		
    		if( is_selected) {
    			this.prompt = prompt;
    			return false;
    		}
    		return true;
    	}
    	
    	public String getPrompt(){
    		return prompt;
    	}
	};
	
	
	
		
	public static SelectiveField createSelectiveField(CompositeMap view){
		SelectiveFieldImpl fld = new SelectiveFieldImpl();
		fld.initialize(view);
		return fld;
	}


	/**
	 * @see org.lwap.ui.SelectiveField#getDataSource()
	 */
	public String getDataSource() {
		return getString( DataBindingConvention.KEY_DATASOURCE);
	}

	/**
	 * @see org.lwap.ui.SelectiveField#setDataSource(String)
	 */
	public void setDataSource(String source) {
		putString( DataBindingConvention.KEY_DATASOURCE, source);
	}

	/**
	 * @see org.lwap.ui.SelectiveField#getDisplayField()
	 */
	public String getDisplayField() {
		return getString(DataBindingConvention.KEY_DISPLAY_FIELD);
	}

	/**
	 * @see org.lwap.ui.SelectiveField#setDisplayField(String)
	 */
	public void setDisplayField(String field) {
		putString(DataBindingConvention.KEY_DISPLAY_FIELD, field);
	}

	/**
	 * @see org.lwap.ui.SelectiveField#getValueField()
	 */
	public String getValueField() {
		return getString( DataBindingConvention.KEY_VALUE_FIELD);
	}

	/**
	 * @see org.lwap.ui.SelectiveField#setValueField(String)
	 */
	public void setValueField(String field) {
		putString( DataBindingConvention.KEY_VALUE_FIELD, field);
	}
	
	public Collection getOptions(){
		return getObjectContext().getChilds();
	}
	
	public Collection getBindedOptions(){
		return this.binded_datasource == null?null:this.binded_datasource.getChilds();
	}
	
	boolean isValueEqual( Object value, Object compared){
		if( value == null) return compared == null;
		else if(compared == null) return false;
		else return value.toString().equals(compared.toString());
	}
	
	public void forEachOption( SelectiveField.SelectOptionProcessor processor){

		Object value = this.getValue();
		
		Iterator options = getOptions() == null?null:getOptions().iterator();
		if( options != null)
			while( options.hasNext()){
				CompositeMap option = (CompositeMap)options.next();
				Object o_value = getOptionValue(option);
				String prompt = getOptionPrompt(option);
				if(!processor.processOption(o_value,prompt,isValueEqual(value,o_value))) return;
			}
			
		options = getBindedDataSource()	== null?null:getBindedDataSource().getChildIterator();
		if( options != null)
			while( options.hasNext()){
				CompositeMap option = (CompositeMap)options.next();
				Object o_value = getBindedOptionValue(option);
				if(!processor.processOption(o_value, getBindedOptionPrompt(option), isValueEqual( value, o_value))) return;
			}
			
	}
	
	public CompositeMap createOption( Object value, String prompt){
		CompositeMap option = this.getObjectContext().createChild("option");
		setOptionPrompt(option,prompt);
		setOptionValue(option,value);
		return option;
	}
	
	public String getSelectedPrompt(){
		
		PromptGetter getter = new PromptGetter();
		forEachOption(getter);
		return getter.getPrompt();
		
	}
	
	
	public void bindDataSource( CompositeMap model){
//		if( data_source == null) return;
		this.binded_datasource = DataBindingConvention.getDataSource(model,this.getObjectContext());

	}
	
	public CompositeMap getBindedDataSource(){
		return this.binded_datasource;
	}
	
	public Object getOptionValue( CompositeMap option ){
		return option.get(DataBindingConvention.KEY_DATAVALUE);
	}
	
	public void setOptionValue(CompositeMap option, Object value){
		option.put(DataBindingConvention.KEY_DATAVALUE, value);		
	}
	
	public String getOptionPrompt( CompositeMap option){
		return option.getString( UIAttribute.ATTRIB_PROMPT);
	}
	
	public void setOptionPrompt( CompositeMap option, String prompt){
		option.put( UIAttribute.ATTRIB_PROMPT, prompt);
	}
	
	
	public Object getBindedOptionValue( CompositeMap option ){
		return option.getObject(this.getObjectContext().getString(DataBindingConvention.KEY_VALUE_FIELD) );
//		return DataBindingConvention.getValueField(option, this.getObjectContext());
	}
	
	public String getBindedOptionPrompt( CompositeMap option){
		Object prompt =option.getObject(this.getObjectContext().getString(DataBindingConvention.KEY_DISPLAY_FIELD) );
		return prompt == null?"":prompt.toString();
//		return DataBindingConvention.getDisplayField(option, this.getObjectContext());
	}

	/**
	 * @see uncertain.composite.DynamicObject#initialize(CompositeMap)
	 */
	public DynamicObject initialize(CompositeMap context)
		throws ClassCastException {
		
		super.initialize(context);		
		
		Iterator childs = context.getChildIterator();
		if( childs == null) return this;
		
		Class data_type = getDataTypeClass();
		if(data_type != null)
			if( !data_type.equals(String.class))
				while( childs.hasNext()){
					CompositeMap option = (CompositeMap) childs.next();
					Object value = getOptionValue(option);
					try{
						value = DataTypeManager.parseObject(data_type,value);
						setOptionValue( option, value);
					}catch(Exception ex){
						continue;
					}
				}
		return this;		
		
	}
/*	
	public void setValue(Object value) {
		if( this.isReadOnly()) throw new NullPointerException();
		super.setValue(value);
	}
*/	

}
