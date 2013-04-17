/*
 * ResultSetLoader.java
 *
 * Created on 2002��4��5��, ����4:19
 */

package org.lwap.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ResultSetLoader {
    
    static int RECORD_COUNT_WARNING = 5000;
    static final String KEY_SOURCE = "____RESULT_SET_SOURCE____";
    
    ResultSetMetaData      rs_meta;
    public static final String CASE_UPPER = "upper";
    public static final String CASE_LOWER = "lower";
    
    String  attribute_case;
    
    String                      source = "Unknown";
    ILogger                     logger;    
    
    /**
     * Set character case of key for map 
     * @param _case "lower" or "upper", null for uneffected
     */
    public void setKeyCase(String _case){
        this.attribute_case = _case;
    }

    public ResultSetLoader( ResultSetMetaData rsmd ) {
        
        rs_meta = rsmd;
    }
    
    public ResultSetLoader( ResultSet rs ) throws SQLException {
         rs_meta = rs.getMetaData();
    }
    
    public CompositeMap load( CompositeMap map, ResultSet rs)  throws SQLException  {    	
        for (int i=1; i<=rs_meta.getColumnCount() ; i++){
            DatabaseTypeField fld = DataTypeManager.getType(rs_meta.getColumnType(i));
            if(fld == null) {
            	System.out.println("unknown type:" + rs_meta.getColumnType(i));
            	continue;
            }
            // ------------------ set case -----------------------
            String column = rs_meta.getColumnName(i);
            if(attribute_case!=null){
                if(CASE_UPPER.equalsIgnoreCase(attribute_case))
                    column = column.toUpperCase();
                else if(CASE_LOWER.equalsIgnoreCase(attribute_case))
                    column = column.toLowerCase();
            }
            map.put( column,  fld.getObject(rs, i) );
        }        
        return map;
    }

    public CompositeMap load( String name, ResultSet  rs ) throws SQLException {
        CompositeMap    map = new CompositeMap( (int)(rs_meta.getColumnCount()/0.75));
        map.setName(name);
        return load( map, rs);
    }
    
    /**
     *@param map parent CompositeMap node
     *@param child_name name of each child node
     *@param rs ResultSet that load from
     *@param offset offset of the ResultSet
     *@param count count of records to load
     */
    public CompositeMap loadList( CompositeMap map, String child_name, ResultSet rs, 
                                                       long offset, long count) throws SQLException {

        if( offset==0){
        	if(!rs.next()) return map;
        } else{
	        for( long i=0; i<=offset; i++){
	            if(!rs.next()) return map;
	        }
        }
        
        for( long i=0; i<count; i++){
            map.addChild( load( child_name, rs));
            if(!rs.next()) return map;
        }
        return map;
    }
    
    public CompositeMap loadList( CompositeMap map, String child_name, ResultSet rs) throws SQLException {
        int size=0;
    	while(rs.next()){
    		map.addChild( load(child_name, rs));
    		size++;
    		if(size>RECORD_COUNT_WARNING){
    		    warnResultSetSize();
    		    map.put(KEY_SOURCE, source);
    		}
    	}
        if(size>RECORD_COUNT_WARNING)
            warnResultSetSize(size);    	
    	return map;
    }
    
   
    public static CompositeMap loadList( Statement stmt, String sql, String child_name, String root_name) throws SQLException {
    	ResultSet rs = stmt.executeQuery(sql);
    	CompositeMap map = new CompositeMap(root_name);
    	try{
      	 ResultSetLoader rsl = new ResultSetLoader(rs);         
    	 rsl.loadList(map, child_name, rs);
    	} catch(  SQLException ex){
    		return map;
    	}
    	rs.close();
    	return map;
    }

    public String getSource() {
        return source;
    }

    public ILogger getLogger() {
        return logger;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
    
    public void warnResultSetSize(){
        String s = "Record count from " + source + " is exceeding warning level of " + RECORD_COUNT_WARNING;
        if(logger!=null)
            logger.warning(s);
        else
            System.err.println(s);
    }
    
    public void warnResultSetSize(int size){
        String s = "ResultSet from "+source+" has "+size+" records";
        if(logger!=null)
            logger.warning(s);
        else
            System.err.println(s);
    }

}
