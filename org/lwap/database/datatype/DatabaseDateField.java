package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;

public class DatabaseDateField extends DatabaseTypeField {

    //static DateFormat dateFormat = DateFormat.getDateInstance();
    
    
    /**
 * 
 * @param aName java.lang.String
 */
public DatabaseDateField () {
}
/**
 * 
 * @param aName java.lang.String
 */
public DatabaseDateField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Date.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getDate(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getDate(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.DATE);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "DATE";
}
/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

		aStatement.setDate(anIndex,(Date)anObject);
}

public Object parseString(String value) throws Exception {
    DateFormat df = (DateFormat)DataTypeManager.dateFormat.clone();
    return  new java.sql.Date(df.parse(value).getTime());  
}

public String format( Object obj){
	if( obj == null) return null;
	if( obj instanceof java.util.Date){
        DateFormat df = (DateFormat)DataTypeManager.dateFormat.clone();
		return df.format((java.util.Date)obj);
	}
	return super.format(obj);
}

}
