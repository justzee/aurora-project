/**
 * Created on: 2002-11-28 11:04:51
 * Author:     zhoufan
 */
package org.lwap.ui;

import java.util.Collection;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public interface SelectiveField extends InputField, DataBinding {
	
	public interface SelectOptionProcessor {
		
		/**
		 * Method process select option.
		 * @param value value of option
		 * @param prompt prompt of option
		 * @param is_select is this option selected
		 * @return boolean if false, no further option will be processed
		 */
		public boolean processOption( Object value, String prompt, boolean is_select);
	};
	
	public String getDataSource();
	
	public void setDataSource( String source);
	
	public String getDisplayField();
	
	public void setDisplayField(String field);
	
	public String getValueField();
	
	public void setValueField(String field);
	
	public Collection getOptions();
	
	public Collection getBindedOptions();
	
	public String getSelectedPrompt();

	public CompositeMap createOption( Object value, String prompt);
	
	public void bindDataSource( CompositeMap model);
	
	public CompositeMap getBindedDataSource();

	public Object getOptionValue( CompositeMap option );
	
	public void setOptionValue(CompositeMap option, Object value);
	
	public String getOptionPrompt( CompositeMap option);
	
	public void setOptionPrompt( CompositeMap option, String prompt);
	
	
	public Object getBindedOptionValue( CompositeMap option );
	
	public String getBindedOptionPrompt( CompositeMap option);
	
	public void forEachOption( SelectOptionProcessor processor);
	

}
