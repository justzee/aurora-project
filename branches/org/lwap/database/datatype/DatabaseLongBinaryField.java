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
public class DatabaseLongBinaryField extends DatabaseTypeField {
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseLongBinaryField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseLongBinaryField (String aName ) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return byte[].class;
}
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getBytes(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getBytes(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.LONGVARBINARY);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "LONGVARBINARY";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

	java.io.ByteArrayInputStream stream;
	stream = new java.io.ByteArrayInputStream((byte[])anObject);
	aStatement.setBinaryStream(anIndex,stream,stream.available());	
}

public Object parseString(String vl) throws Exception {
    return vl.getBytes();
}

}
