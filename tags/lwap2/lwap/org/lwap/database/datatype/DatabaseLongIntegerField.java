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
public class DatabaseLongIntegerField extends DatabaseTypeField {
	// VersionUID for Version 1.0
	static final long serialVersionUID = 2682057044304580564L;
	private final static String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 1998, 2000";
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseLongIntegerField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseLongIntegerField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Long.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return new Long(aCallableStatement.getLong(anIndex));
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return new Long(aResultSet.getLong(anIndex));
}
/**
 * 
 */
public long getPrimObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getLong(anIndex);
}
/**
 * 
 */
public long getPrimObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getLong(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.BIGINT);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "BIGINT";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
	
		aStatement.setLong(anIndex,((Number)anObject).longValue());
}
/**
 * 
 */
public void setPrimObject (long aLong,PreparedStatement aStatement,int anIndex) throws SQLException{
	
	aStatement.setLong(anIndex,aLong);
}

public Object parseString(String vl) throws Exception {
    return new Long( vl);
}

}
