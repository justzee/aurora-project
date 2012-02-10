/*
 * SQLStatement.java
 *
 * Created on 2002年8月8日, 下午8:15
 */

package org.lwap.database;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  zhoufan
 */
public abstract class SQLStatement {
    
    public static final String KEYWORD_AND = "and";
    public static final String KEYWORD_OR   = "or";
    public static final String KEYWORD_NOT = "not";
    
     String         object_name;
     String         schema_name;
    WhereClause where_clause = new WhereClause();
    
     public abstract String getSQL();
     
     public SQLStatement( String _schema_name, String _object_name){
         schema_name = _schema_name;
         object_name = _object_name;
     }
     
     public SQLStatement( String _object_name){
         this( null, _object_name);
     }
     
     public SQLStatement(){
     }
              
     public void setObjectName( String _obj_name){
         object_name = _obj_name;
     }
     
     public void setSchemaName( String _schema_name){
         schema_name = _schema_name;
     }
     
     public WhereClause getWhereClause(){ return where_clause ;  }
     
     public String getObjectName(){
         if( schema_name != null) return schema_name + '.' + object_name;
         else return object_name;
     }
     
     public static String connectFields( Collection cl, String cstr ){
    	StringBuffer sql = new StringBuffer();
    	int count = 0;
    	
    	if ( cl == null) return null;
    	Iterator it = cl.iterator();
    	while( it.hasNext()){
    		String  str = it.next().toString();
    		if( count>0) sql.append(cstr);
    		sql.append(str);
    		count++;
    	}
    	
    	return sql.toString();
     }
     
     public static String connectFields( Collection cl ){
     	return connectFields( cl, ",");
     }
     


}
