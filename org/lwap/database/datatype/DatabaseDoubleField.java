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
public class DatabaseDoubleField extends DatabaseTypeField {
        
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseDoubleField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseDoubleField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Double.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return new Double(aCallableStatement.getDouble(anIndex));
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return new Double(aResultSet.getDouble(anIndex));
}
/**
 * 
 */
public double getPrimObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getDouble(anIndex);
}
/**
 * 
 */
public double getPrimObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getDouble(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.DOUBLE);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "DOUBLE";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
	
		aStatement.setDouble(anIndex,((Number)anObject).doubleValue());
}	
/**
 * 
 */
public void setPrimObject (double aDouble,PreparedStatement aStatement,int anIndex) throws SQLException{
	
	aStatement.setDouble(anIndex,aDouble);
}

public Object parseString(String vl) throws Exception {
    return new Double(vl);
}

}
