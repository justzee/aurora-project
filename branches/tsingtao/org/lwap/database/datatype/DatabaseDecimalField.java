package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
public class DatabaseDecimalField extends DatabaseTypeField {
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseDecimalField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseDecimalField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return BigDecimal.class;
}
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getBigDecimal(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getBigDecimal(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.DECIMAL);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "DECIMAL";
}
/**
 * This method registers this field with the CallableStatement.
 */
public void registerParam (CallableStatement aStatement,int anIndex) throws SQLException {
	
	aStatement.registerOutParameter(anIndex,getSQLType());
}
/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

		aStatement.setBigDecimal(anIndex,(BigDecimal)anObject);
}

public Object parseString(String vl) throws Exception {
    return new BigDecimal(vl);
}

}
