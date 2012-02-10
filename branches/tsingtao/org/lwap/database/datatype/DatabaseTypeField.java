package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public abstract class DatabaseTypeField {


private String name;

private int length, scale;

private boolean nullOK = true;

public int sqlType;



private int paramType = DatabaseMetaData.procedureColumnIn;

private Object value;

public DatabaseTypeField () {
}

public DatabaseTypeField ( String aName) {
name = aName;
}
/**
 * 
 * @return java.lang.Object
 */
protected Object clone ( ) throws CloneNotSupportedException {
	return super.clone();
}
/**
 * 
 * @return java.lang.Class
 */
public abstract Class getFieldClass ( );
/**
 * 
 */
public int getLength () {
	return length;
}
/**
 * 
 */
public String getName () {
	return name;
}
/**
 * 
 */
public boolean getNullable () {
	return nullOK;
}

/** parse an object from string */
public Object parseObject( String vl) throws java.text.ParseException {
    if( vl == null) return null;    
    else
        try{
            return parseString(vl);
        }catch (Exception ex){
            throw new ParseException(ex.getMessage(),0);
        }
}

protected abstract Object parseString( String vl) throws Exception ;

/**
 * 
 * @param aName java.lang.String
 */
public abstract Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException;
/**
 * 
 * @param aName java.lang.String
 */
public abstract Object getObject (ResultSet aResultSet,int anIndex) throws SQLException;
/**
 * 
 */
public int getParamType (){
	return paramType;
}
/**
 * 
 */
public int getScale (){
	return scale;
}
/**
 * 
 */
public abstract int getSQLType ();
/**
 * This method registers this field with the CallableStatement.
 */
public void registerParam (CallableStatement aStatement,int anIndex) throws SQLException {
	
	aStatement.registerOutParameter(anIndex,getSQLType());
}
/**
 * 
 */
public  DatabaseTypeField setAttributes (int aLength,int aScale,int sqlDataType,boolean nullOK) {
	this.setLength(aLength);
	this.setScale(aScale);
	this.setNullable(nullOK);
	this.setSQLType(sqlDataType);
	return this;
}
/**
 * 
 */
public  DatabaseTypeField setAttributes (int aLength,int aScale,int sqlDataType,boolean nullOK,int paramType) {
	this.setLength(aLength);
	this.setScale(aScale);
	this.setNullable(nullOK);
	this.setSQLType(sqlDataType);
	this.setParamType(paramType);
	return this;
}
/**
 * 
 */
public  DatabaseTypeField setAttributes (int aLength,int aScale,int sqlDataType,boolean nullOK,short paramType) {
	this.setLength(aLength);
	this.setScale(aScale);
	this.setNullable(nullOK);
	this.setSQLType(sqlDataType);
	this.setParamType(paramType);
	return this;
}
/**
 * 
 */
public  DatabaseTypeField setAttributes (int aLength,int aScale,boolean nullOK) {
	this.setLength(aLength);
	this.setScale(aScale);
	this.setNullable(nullOK);
	return this;
}
/**
 * 
 * @param aName java.lang.String
 */
public abstract void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException;
/**
 * 
 */
public void setLength (int aLength) {
	length = aLength;
}
/**
 * 
 */
public void setName (String aName) {
	name = aName;
}
/**
 * 
 */
public void  setNullable(boolean aValue) {
	nullOK = aValue;
}
/**
 * This method sets the object value for this field, if the field is set to null, the appropriate JDBC method is sent(setNull)
 */
public void setObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException {
	
	if (anObject == null) 
		aStatement.setNull(anIndex,getSQLType());
	else
		setFieldObject(anObject,aStatement,anIndex);
}
/**
 * 
 */
public void setParamType (int aParamType){
	paramType = aParamType;
}
/**
 * 
 */
public void setScale (int aValue) {
	scale = aValue;
}
/**
 * 
 */
public void setSQLType (int anSQLType) {
	sqlType = anSQLType;
}

public Object getValue(){
    return value;
}

public void setValue(Object v){
    value = v;
}


public void parseValue( String vl) throws ParseException {
    value = parseObject(vl);
}


public String format( Object obj) {
	if( obj == null) return null;
	else return obj.toString();
}

}
