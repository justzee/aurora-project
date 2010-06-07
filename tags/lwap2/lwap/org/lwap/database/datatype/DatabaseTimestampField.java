package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;

public class DatabaseTimestampField extends DatabaseTypeField {

//static DateFormat dateFmt =DateFormat.getDateInstance(), datetimeFormat = DateFormat.getDateTimeInstance(); 

        /**
 * 
 * @param aName java.lang.String
 */
public DatabaseTimestampField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseTimestampField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Timestamp.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getTimestamp(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getTimestamp(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.TIMESTAMP);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "TIMESTAMP";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

		aStatement.setTimestamp(anIndex,(Timestamp)anObject);
}

public Object parseString(String value) throws Exception {
	   if( value == null) return null;
       DateFormat df = null;
       try{
           df = (DateFormat)DataTypeManager.datetimeFormat.clone();
           return new Timestamp(  df.parse(value).getTime() );
        } 
        catch(ParseException pex){
           df = (DateFormat)DataTypeManager.dateFormat.clone();
           return new Timestamp( df.parse(value).getTime());
        }    
}

public String format( Object obj){
	if( obj == null) return null;
	if( obj instanceof java.util.Date){
        
        DateFormat df = (DateFormat)DataTypeManager.datetimeFormat.clone();
//        System.out.println("format:"+df);
        return df.format( new Timestamp(((java.util.Date)obj).getTime()));
	}
	return super.format(obj);
}

}
