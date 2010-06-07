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
public class DatabaseFloatField extends DatabaseTypeField {

    /**
 * 
 * @param aName java.lang.String
 */
public DatabaseFloatField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseFloatField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Float.class;
}	


/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return new Float(aCallableStatement.getFloat(anIndex));
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return new Float(aResultSet.getFloat(anIndex));
}
/**
 * 
 */
public float getPrimObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getFloat(anIndex);
}
/**
 * 
 */
public float getPrimObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getFloat(anIndex);
}
/**
 * 
 */
public int getSQLType () {
	return Types.FLOAT;}

public String getSQLTypeName() {
	return "FLOAT";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{
	
		aStatement.setFloat(anIndex,((Number)anObject).floatValue());
}
/**
 * 
 */
public void setPrimObject (float aFloat,PreparedStatement aStatement,int anIndex) throws SQLException{
	
	aStatement.setFloat(anIndex,aFloat);
}

public Object parseString(String vl) throws Exception {
    return new Float(vl);
}

}
