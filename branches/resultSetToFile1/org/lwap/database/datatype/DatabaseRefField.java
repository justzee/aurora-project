package org.lwap.database.datatype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
/**
 * Insert the type's description here.
 * Creation date: (4/27/2000 2:36:20 PM)
 * @author: 
 */
public class DatabaseRefField extends DatabaseTypeField {
	
/**
 * DatabaseRefField constructor comment.
 */
public DatabaseRefField() {
	super();
}
/**
 * DatabaseRefField constructor comment.
 * @param aName java.lang.String
 */
public DatabaseRefField(String aName) {
	super(aName);
}
/**
 * getFieldClass method comment.
 */
public Class getFieldClass() {
	return Ref.class;
}	
/**
 * getObject method comment.
 */
public Object getObject(CallableStatement aCallableStatement, int anIndex) throws SQLException {
	
	return aCallableStatement.getRef(anIndex);
}
/**
 * getObject method comment.
 */
public Object getObject(ResultSet aResultSet, int anIndex) throws SQLException {
	
	return aResultSet.getRef(anIndex);
}
/**
 * getSQLType method comment.
 */
public int getSQLType() {
	
	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.REF);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "REF";
}

/**
 * setFieldObject method comment.
 */
public void setFieldObject(Object anObject, PreparedStatement aStatement, int anIndex) throws SQLException {
	
	aStatement.setRef(anIndex,(Ref)anObject);
}

public Object parseString(String vl) throws Exception {
         return null;
}

}
