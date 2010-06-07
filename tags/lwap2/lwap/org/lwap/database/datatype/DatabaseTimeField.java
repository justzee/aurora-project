package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.text.DateFormat;

public class DatabaseTimeField extends DatabaseTypeField {
   /* 
    static DateFormat time_format = DateFormat.getTimeInstance();
    
    DateFormat format = time_format;
    */
/*
 * 
 * @param aName java.lang.String
 */
public DatabaseTimeField () {
}

public void setDateFormat(DateFormat fmt){
    // format = fmt;
}


/**
 * 
 * @param aName java.lang.String
 */
public DatabaseTimeField ( String aName) {
super(aName);
}
/**
 * This method returns the Java class used to hold a value of this field.
 * @return java.lang.Class
 */
public Class getFieldClass ( ) {
	return Time.class;
}	
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	return aCallableStatement.getTime(anIndex);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	return aResultSet.getTime(anIndex);
}
/**
 * 
 */
public int getSQLType () {

	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.TIME);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "TIME";
}

/**
 * 
 */
public void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException{

		aStatement.setTime(anIndex,(Time)anObject);
}

public Object parseString(String vl) throws Exception {
                return new java.sql.Time(DataTypeManager.time_format.parse(vl).getTime());
}

public String format( Object obj){
	if( obj == null) return null;
	if( obj instanceof java.sql.Time){
		return DataTypeManager.time_format.format((java.sql.Time)obj);
	}
	return super.format(obj);
}

}
