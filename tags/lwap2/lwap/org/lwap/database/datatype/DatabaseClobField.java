package org.lwap.database.datatype;

/**
 * Insert the type's description here.
 * Creation date: (4/26/2000 9:29:31 PM)
 * @author: 
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
public class DatabaseClobField extends DatabaseTypeField {
	
/**
 * DatabaseClobField constructor comment.
 */
public DatabaseClobField() {
	super();
}
/**
 * DatabaseClobField constructor comment.
 * @param aName java.lang.String
 */
public DatabaseClobField(String aName) {
	super(aName);
}
/**
 * getFieldClass method comment.
 */
public Class getFieldClass() {
	return Clob.class;
}
/**
 * getObject method comment.
 */
public Object getObject(java.sql.CallableStatement aCallableStatement, int anIndex) throws java.sql.SQLException {
/*	
	Clob aClob = aCallableStatement.getClob(anIndex);
	int clobLength = (int)aClob.length();
	return aClob.getSubString(1,clobLength);
*/
    return aCallableStatement.getClob(anIndex);
}
/**
 * getObject method comment.
 */
public Object getObject(java.sql.ResultSet aResultSet, int anIndex) throws java.sql.SQLException {

	Clob aClob = aResultSet.getClob(anIndex);
	if( aClob == null) return null;
	try{
		StringBuffer buf = new StringBuffer();
		Reader reader = aClob.getCharacterStream();
		if( reader == null) return null;
		int n;
		while( ( n = reader.read()) != -1) buf.append((char)n);
		return buf.toString();
	} catch(IOException ex){
        ex.printStackTrace();
		return null;
	}
	/**/
    //return aResultSet.getClob(anIndex);
}
/**
 * getSQLType method comment.
 */
public int getSQLType() {	
	return Types.CLOB;
}

public String getSQLTypeName() {
	return "CLOB";
}

public void setFieldObject(Object anObject, PreparedStatement aStatement,
        int anIndex) throws SQLException {
    if(anObject instanceof String)
        aStatement.setString(anIndex, (String)anObject);
    else if( anObject instanceof InputStream){
        aStatement.setAsciiStream(anIndex, (InputStream)anObject, anIndex);
    }
    else if( anObject instanceof Clob)
        aStatement.setClob(anIndex, (Clob)anObject);
    else if( anObject instanceof Blob)
        aStatement.setBlob(anIndex, (Blob)anObject);
}

protected Object parseString(String vl) throws Exception {
/*
    ByteArrayInputStream bis = new ByteArrayInputStream(vl.getBytes());
    return bis;
*/
    return vl;
}

}
