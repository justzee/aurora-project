/**
 * Created on: 2002-11-28 11:02:50
 * Author:     zhoufan
 */
package org.lwap.ui;

import uncertain.composite.CompositeMap;

public interface DataBinding {
	
	public String getDataField();
	
	public void setDataField(String field);
	
	public String getDataModel();
	
	public void setDataModel(String model);
	
	public void bindModel( CompositeMap model);
	
	public CompositeMap getBindedModel();

}
