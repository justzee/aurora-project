
package org.lwap.mvc;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

public class DataBindingConvention {
	
	public static final String EMPTY_STRING = "";
	public static final String KEY_DATAMODEL = "dataModel";
	public static final String KEY_DATASOURCE = "dataSource";
	public static final String KEY_DATAFIELD = "dataField";
	public static final String KEY_DATAVALUE = "dataValue";
	public static final String KEY_DISPLAY_FIELD = "displayField";
	public static final String KEY_VALUE_FIELD = "valueField";		
//	public static final String KEY_VALUE = "value";
	public static final String KEY_ENTITY 		= "Entity";
	

	
	public static final String[] VALUE_SEQUENCE = { KEY_DATAVALUE, KEY_DATAFIELD};
	
	static boolean print_debug_info = false;
	
	public static void setPrintDebugInfo( boolean b){
		print_debug_info = b;
	}
	
	static void printDebugInfo( String info){
		if(print_debug_info){
			System.out.println("[DataBindingConvention] "+info);
		}
	}
	
	static Object getObject( CompositeMap model, String key){
		if( model == null || key == null) return null;
	        return model.getObject(key);
	}	
	
	static Object getObject( CompositeMap model, String key, String debug_info){
		Object obj = getObject(model,key);
		if( obj == null) printDebugInfo(debug_info);
		return obj;
	}
	
	static String non_null_string( Object obj){
		if( obj == null) return EMPTY_STRING;
		else return obj.toString();
	}
	

	/** get model specified in "dataModel=" attribute of view */
	public static CompositeMap getDataModel( CompositeMap model, CompositeMap view){
		 if( model == null){
		 	printDebugInfo("getDataModel() got null model"); 
		 	return null;
		 }	
		 if( view != null){
		 	String dataModel = view.getString(KEY_DATAMODEL);
		 	if( dataModel == null)
		 		return model;
		 	else{		 		
		 		CompositeMap m = (CompositeMap)model.getObject(dataModel);
		 		if( m == null){ 
		 			printDebugInfo("can't get model by key "+dataModel); 
		 			return null;
		 		}else return m;
		 	}
		 }
		 else{ 
		 	printDebugInfo("getDataModel() got null view"); 
		 	return model;
		 }
/*		 
		 Object obj =    getObject( model,  );
		 if( obj == null) return model;
		 else return (CompositeMap)obj;
*/		 
	}

	
	/** get an object from model that is stored with key name specified by the view's 
	 *  "dataField" attribute 
	 */
	public static Object getDataField( CompositeMap model, CompositeMap view ){
		if( view == null) return EMPTY_STRING;
		String data_field = view.getString( KEY_DATAFIELD);
		return getObject( model, data_field);
	}
	
	
	/** For control that take a single value, either from specified attribute in model, 
	 *  or by predefined attribute value in view.
	 * 
	 *  Model:<pre><employee Name="James"/></pre> View: <pre><textedit dataValue="Enter name here" /></pre> 
	 *  getDataValue(model,view) = "Enter name here"
	 *
	 *  Model:<pre><employee Name="James"/></pre> View: <pre><textedit dataField="@James" /></pre> 
	 *  getDataValue(model,view) = "James"
	 */ 
	public static String getDataValue(CompositeMap model, CompositeMap view ){
		if( view == null) return EMPTY_STRING;
		
		String str = view.getString( KEY_DATAVALUE );
		if( str != null) 
			return str;
		else
			return non_null_string(getDataField( model, view));	
	}
	
	/**
	 *  For "select" like controls, get element value from model that is stored
	 *  with key specified in the view's "valueField" attribute
	 */
	public static String getValueField( CompositeMap data_source_item, CompositeMap view){
		if( data_source_item == null || view == null ) return EMPTY_STRING;		
		return non_null_string(data_source_item.getObject( view.getString(KEY_VALUE_FIELD)) );
	}
	
	/**
	 *  For "select" like controls, get element display value from model that is stored
	 *  with key specified in the view's "displayField" attribute
	 */
	public static String getDisplayField( CompositeMap data_source_item, CompositeMap view){
		if( data_source_item == null|| view == null ) return EMPTY_STRING;
		return non_null_string(data_source_item.getObject( view.getString(KEY_DISPLAY_FIELD)) );
	}
	
	
	/**
	 *  For "select" like controls, get CompositeMap that contains available select options
	 *  from model that is stored with key specified in the "dataSource" attribute of view
	 */	
	public static CompositeMap getDataSource( CompositeMap model, CompositeMap view){
		if( model == null|| view == null ) return null;
		String data_source = view.getString(KEY_DATASOURCE);
		if( data_source == null) {
			printDebugInfo(KEY_DATASOURCE +" not set in view: "+view.toXML());
			return null;
		}
		Object obj =    getObject( model, data_source , "can't get DataSource by "+ data_source);
		return (CompositeMap)obj;		 
	}
	
	/**
	 *  Gets a attribute value from view, then return parsed content on model.
	 *  For examples: 
	 *  model: <employee EMPLOYEE_ID="109" />
	 *  view:  <link HRef="EmployeeEdit?employee_id=${@EMPLOYEE_ID} />
	 *  parseAttribute( "HRef", model, view) = "EmployeeEdit?employee_id=109"
	 */
	public static String parseAttribute( String attrib, CompositeMap model, CompositeMap view){
		String value = view.getString(attrib);
		return value==null?EMPTY_STRING:TextParser.parse(value,model);		
	}
	
	
	
	public static void main(String[] args){
		CompositeMap model = new CompositeMap(), view = new CompositeMap();
		
		CompositeMap child = model.createChild(null,null,"employee");
		child.put("name","James");
		
		model.put("name", "Hery");
		
		view.put(KEY_DATAFIELD, "@name");
		System.out.println( getDataValue( child, view));
		
		view.put(KEY_DATAVALUE, "/@name");
		System.out.println( getDataValue( child, view));
		
		view.put(KEY_DATAMODEL, "/employee");
		System.out.println( getDataModel(model, view));
		
	}
}