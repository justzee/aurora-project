package org.lwap.database.datatype;

import java.sql.Array;
import java.sql.Types;
/**
 * Insert the type's description here.
 * Creation date: (4/26/2000 9:28:14 PM)
 * @author: 
 */
public class DatabaseArrayField extends DatabaseTypeField {
	
/**
 * DatabaseArrayField constructor comment.
 */
public DatabaseArrayField() {
	super();
}
/**
 * DatabaseArrayField constructor comment.
 * @param aName java.lang.String
 */
public DatabaseArrayField(String aName) {
	super(aName);
}
/**
 * getFieldClass method comment.
 */
public Class getFieldClass() {
	return Array.class;
}
/**
 * getObject method comment.
 */
public Object getObject(java.sql.CallableStatement aCallableStatement, int anIndex) throws java.sql.SQLException {
	
	return aCallableStatement.getArray(anIndex);
}
/**
 * getObject method comment.
 */
public Object getObject(java.sql.ResultSet aResultSet, int anIndex) throws java.sql.SQLException {
	
	return aResultSet.getArray(anIndex);
}
/**
 * getSQLType method comment.
 */
public int getSQLType() {
	
	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.ARRAY);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "ARRAY";
}

/**
 * setFieldObject method comment.
 */
public void setFieldObject(Object anObject, java.sql.PreparedStatement aStatement, int anIndex) throws java.sql.SQLException {
	
	aStatement.setArray(anIndex,(Array)anObject);
}

public Object parseString(String vl) throws Exception {
    return null;
}

}
