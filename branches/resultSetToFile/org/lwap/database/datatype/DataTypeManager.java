/*
 * DataTypeManager.java
 *
 * Created on 2002年1月14日, 下午3:09
 */

package org.lwap.database.datatype;

import java.sql.Types;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author  Administrator
 * @version 
 */
public class DataTypeManager {
	
	public static DateFormat dateFormat = DateFormat.getDateInstance();
	public static DateFormat datetimeFormat = DateFormat.getDateTimeInstance();
	public static DateFormat time_format = DateFormat.getTimeInstance();
    
    static HashMap type_map = new HashMap();
    
    public static HashMap class_map = new HashMap();
    
    static{
		type_map.put(new Integer(Types.CHAR),new DatabaseStringField());
		type_map.put(new Integer(Types.VARCHAR),new DatabaseStringField());
		type_map.put(new Integer(Types.LONGVARCHAR),new DatabaseLongStringField());
		type_map.put(new Integer(Types.INTEGER),new DatabaseIntegerField());
		type_map.put(new Integer(Types.TINYINT),new DatabaseIntegerField());
		type_map.put(new Integer(Types.SMALLINT),new DatabaseShortIntegerField());
		type_map.put(new Integer(Types.DECIMAL),new DatabaseDecimalField());
		type_map.put(new Integer(Types.NUMERIC),new DatabaseDecimalField());
		type_map.put(new Integer(Types.BIT),new DatabaseBooleanField());
		type_map.put(new Integer(Types.BIGINT),new DatabaseLongIntegerField());
		type_map.put(new Integer(Types.REAL),new DatabaseDoubleField() );
		type_map.put(new Integer(Types.FLOAT),new DatabaseFloatField() );
		type_map.put(new Integer(Types.DOUBLE),new DatabaseDoubleField());
		type_map.put(new Integer(Types.BINARY),new DatabaseBinaryField());
		type_map.put(new Integer(Types.VARBINARY),new DatabaseBinaryField());
		type_map.put(new Integer(Types.LONGVARBINARY),new DatabaseLongBinaryField());
		type_map.put(new Integer(Types.DATE),new DatabaseDateField());
		type_map.put(new Integer(Types.TIME),new DatabaseTimeField());
		type_map.put(new Integer(Types.TIMESTAMP),new DatabaseTimestampField());
		// JDBC 2.0 type_map
		type_map.put(new Integer(Types.ARRAY),new DatabaseArrayField());
		type_map.put(new Integer(Types.BLOB),new DatabaseBlobField());
		type_map.put(new Integer(Types.CLOB),new DatabaseClobField());
		type_map.put(new Integer(Types.REF),new DatabaseRefField());
		//Misc type_map from legacy drivers
		type_map.put(new Integer(-98),new DatabaseLongBinaryField());
		type_map.put(new Integer(-99),new DatabaseLongStringField());
		type_map.put(new Integer(-9),new DatabaseStringField());
		type_map.put(new Integer(-10),new DatabaseLongStringField());
		type_map.put(new Integer(Types.NULL),new DatabaseObjectField());
		type_map.put(new Integer(Types.OTHER),new DatabaseObjectField());
		
		// Oracle rowid
		type_map.put(new Integer(-8), new DatabaseStringField());
                
                Iterator tit = type_map.values().iterator();
                while( tit.hasNext()){
                    DatabaseTypeField field = (DatabaseTypeField) tit.next();
                    class_map.put( field.getFieldClass().getName(), field);
                }
                //extra type mapping
                class_map.put("java.util.Date", class_map.get("java.sql.Date"));
                class_map.put("oracle.sql.ARRAY", class_map.get("java.sql.Array"));
                class_map.put("java.io.ByteArrayInputStream", class_map.get("java.sql.Clob"));        
    }
    
    public static void registerType( int sql_type, Class java_type, DatabaseTypeField instance ){
        type_map.put( new Integer(sql_type),instance );
        class_map.put( java_type.getName(), instance );
    }
   
    public static DatabaseTypeField getType(int id){
        return (DatabaseTypeField)type_map.get( new Integer(id));
    }
    
    public static DatabaseTypeField getType( String cls_name){
        return (DatabaseTypeField) class_map.get( cls_name );
    }
    
    public static DatabaseTypeField getType( Class cls){
        return (DatabaseTypeField) class_map.get( cls.getName());
    }
    
    public static DatabaseTypeField getType( Object obj){
       return  getType( obj.getClass());
    }
    
    public static Class getClassByName( String cls_name){
    	DatabaseTypeField fld = getType(cls_name);
    	return fld == null?null:fld.getFieldClass();
    }
    
    public static Object parseObject( Class data_type, Object value)
    	throws java.text.ParseException
    {
    	if( value == null || data_type == null) return value;
    	DatabaseTypeField dt = getType(data_type);
    	if( dt == null) return value;
    	//if( value instanceof dt.getFieldClass()) return value;
    	if( value instanceof String){
    		if( dt.getFieldClass() == String.class) return value;
    		return dt.parseObject((String)value);
    	}else return value;
    }
    
    public static Object parseObject( String data_type_name, Object value)
    	throws java.text.ParseException
    {
    	Class cls = getClassByName(data_type_name);
    	if( cls == null) throw new IllegalArgumentException("Unknown data type:"+data_type_name);
    	return parseObject( cls,value);
    }
    
/*    
    public static TypedParameter getTypedParameter( Object obj){
       return new TypedParameter( getDatabaseTypeField(obj), obj);
    }
 */
    public static HashMap getTypeMap(){
    	return type_map;
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(DataTypeManager.getType("java.lang.Long" ));
       // System.out.println(type_map);
    }
    
}
