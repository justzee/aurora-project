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
public class DatabaseObjectField extends DatabaseTypeField {
/**
 * 
 */
public DatabaseObjectField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseObjectField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Object.class;
}
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getObject(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getObject(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	return Types.JAVA_OBJECT;
}

public String getSQLTypeName() {
	return "JAVA_OBJECT";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
	int sqlDataType = getSQLType();
	if (sqlDataType == 0)
		aStatement.setObject(anIndex,anObject);
	else	
		aStatement.setObject(anIndex,anObject,sqlDataType);
		
}

public Object parseString(String vl) throws Exception {
    return vl;
}

}
