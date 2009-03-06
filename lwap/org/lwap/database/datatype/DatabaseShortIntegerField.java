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
public class DatabaseShortIntegerField extends DatabaseTypeField {
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseShortIntegerField () {
}	
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseShortIntegerField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Short.class;
}		
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return new Short(aCallableStatement.getShort(anIndex));
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return new Short(aResultSet.getShort(anIndex));
}
/**
 * 
 */
public short getPrimObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getShort(anIndex);
}
/**
 * 
 */
public short getPrimObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getShort(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.SMALLINT);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "SMALLINT";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

	aStatement.setShort(anIndex,((Number)anObject).shortValue());
}
/**
 * 
 */
public void setPrimObject (short aShort,PreparedStatement aStatement,int anIndex) throws SQLException{
	
	aStatement.setShort(anIndex,aShort);
}

public Object parseString(String vl) throws Exception {
        return new Short(vl);
}

}
