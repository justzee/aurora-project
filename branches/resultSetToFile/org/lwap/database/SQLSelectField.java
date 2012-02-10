/*
 * SQLSelectField.java
 *
 * Created on 2002年8月8日, 下午8:19
 */

package org.lwap.database;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  zhoufan
 */
public class SQLSelectField {
    
    String            field_name;
    String            field_alias;
    String            entity_name;
    
    /** Creates a new instance of SQLSelectField */
    public SQLSelectField() {
    }
    
    public void setFieldName( String _field_name){
        
    }
    
    public String getSQLStatement(){
    	StringBuffer sql = new StringBuffer(field_name);
    	if( field_alias != null)  
    		if(!field_alias.equalsIgnoreCase(field_name))
    			sql.append(" as ").append(field_alias);
    	return sql.toString();
    }
    
    public static String getFieldList( Collection list) {
    	
    	StringBuffer sql = new StringBuffer();
    	int count = 0;
    	
    	if ( list == null) return null;
    	Iterator it = list.iterator();
    	while( it.hasNext()){
    		SQLSelectField fld = (SQLSelectField)it.next();
    		if( count>0) sql.append(',');
    		sql.append(fld.getSQLStatement());
    		count++;
    	}
    	
    	return sql.toString();
    	
    }
    
    
}
