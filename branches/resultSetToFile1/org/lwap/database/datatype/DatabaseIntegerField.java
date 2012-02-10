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
public class DatabaseIntegerField extends DatabaseTypeField {
	// VersionUID for Version 1.0
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseIntegerField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseIntegerField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Integer.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return new Integer(aCallableStatement.getInt(anIndex));
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return new Integer(aResultSet.getInt(anIndex));
}
/**
 * 
 */
public int getPrimObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getInt(anIndex);
}
/**
 * 
 */
public int getPrimObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getInt(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.INTEGER);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "INTEGER";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
	
		aStatement.setInt(anIndex,((Number)anObject).intValue());
}
/**
 * 
 */
public void setPrimObject (int anInt,PreparedStatement aStatement,int anIndex) throws SQLException{
	
	aStatement.setInt(anIndex,anInt);
}

public Object parseString(String vl) throws Exception {
        return new Integer(vl);
}

}
