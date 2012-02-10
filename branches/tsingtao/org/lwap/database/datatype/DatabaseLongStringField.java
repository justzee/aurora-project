package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
public class DatabaseLongStringField extends DatabaseTypeField {
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseLongStringField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseLongStringField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return String.class;
}
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getString(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{
	
	return aResultSet.getString(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.LONGVARCHAR);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "LONGVARCHAR";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
/*
	java.io.ByteArrayInputStream stream;
	stream = new java.io.ByteArrayInputStream(((String) anObject).getBytes());
	aStatement.setAsciiStream(anIndex,stream,stream.available());			
*/
	aStatement.setString(anIndex,(String)anObject);
}	

public Object parseString(String vl) throws Exception {
    return vl;
}

}
