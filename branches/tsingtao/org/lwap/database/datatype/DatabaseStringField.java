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
public class DatabaseStringField extends DatabaseTypeField {
	// VersionUID for Version 1.0
	static final long serialVersionUID = 350424674362016243L;
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseStringField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseStringField ( String aName) {
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
 * @return java.lang.Object
 * @param aCallableStatement CallableStatement
 * @param anIndex int
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getString(anIndex);
}
/**
 * 
 * @return java.lang.Object
 * @param aResultSet ResultSet
 * @param anIndex int
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getString(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.CHAR);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "CHAR";
}

/**
 * 
 * @param aResultSet ResultSet
 * @param colIndex java.lang.Integer
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
	int sqlDataType = getSQLType();
	if (sqlDataType == Types.CHAR) 
		aStatement.setObject(anIndex,anObject,Types.CHAR);
	else
		aStatement.setString(anIndex,(String)anObject);
}

public Object parseString(String vl) throws Exception {
    return vl;
}

}
