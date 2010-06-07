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
public class DatabaseBooleanField extends DatabaseTypeField {
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseBooleanField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseBooleanField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Boolean.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return new Boolean(aCallableStatement.getBoolean(anIndex));
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return new Boolean(aResultSet.getBoolean(anIndex));
}
/**
 * 
 */
public boolean getPrimObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getBoolean(anIndex);
}
/**
 * 
 */
public boolean getPrimObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getBoolean(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.BIT);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "BIT";
}
/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

		aStatement.setBoolean(anIndex,((Boolean)anObject).booleanValue());
}
/**
 * 
 */
public void setPrimObject (boolean aBoolean,PreparedStatement aStatement,int anIndex) throws SQLException{
	
	aStatement.setBoolean(anIndex,aBoolean);
}

public Object parseString(String vl) throws Exception {
    return new Boolean(vl);
}

}
