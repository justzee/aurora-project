package org.lwap.database.datatype;

/**
 * Insert the type's description here.
 * Creation date: (4/26/2000 9:30:09 PM)
 * @author: 
 */
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
public class DatabaseBlobField extends DatabaseTypeField {
	
/**
 * DatabaseBlobField constructor comment.
 */
public DatabaseBlobField() {
	super();
}
/**
 * DatabaseBlobField constructor comment.
 * @param aName java.lang.String
 */
public DatabaseBlobField(String aName) {
	super(aName);
}
/**
 * getFieldClass method comment.
 */
public Class getFieldClass() {
	return Blob.class;
}
/**
 * 
 */
public Object getObject (CallableStatement aCallableStatement,int anIndex) throws SQLException{

	Blob aBlob = aCallableStatement.getBlob(anIndex);
	int blobLength = (int)aBlob.length();
	return aBlob; //aBlob.getBytes(1,blobLength);
}
/**
 * 
 */
public Object getObject (ResultSet aResultSet,int anIndex) throws SQLException{

	Blob aBlob = aResultSet.getBlob(anIndex);
	int blobLength = (int)aBlob.length();
	return aBlob; //aBlob.getBytes(1,blobLength);
}
/**
 * getSQLType method comment.
 */
public int getSQLType() {
	
	if (sqlType == 0) {	// If an sql type has not been specified, than return the default
		setSQLType(Types.BLOB);
	}
	return sqlType;
}

public String getSQLTypeName() {
	return "BLOB";
}
/**
 * setFieldObject method comment.
 */
public void setFieldObject(Object anObject, java.sql.PreparedStatement aStatement, int anIndex) throws java.sql.SQLException {
	/*
	java.io.ByteArrayInputStream stream;
	stream = new java.io.ByteArrayInputStream((byte[])anObject);
	aStatement.setBinaryStream(anIndex,stream,stream.available());
	*/
    return;
}

public Object parseString(String vl) throws Exception {
    return vl.getBytes();
}

}
