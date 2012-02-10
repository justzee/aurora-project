package org.lwap.database.datatype;

/**
 * 
 * 
 */
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataType {


	public abstract Class getJavaClass();
	
	/** parse an object from string */
	public abstract Object parseString( String vl) throws Exception ;
	
	/** get Object from CallableStatement */
	public abstract Object getObject (CallableStatement stmt, int index ) throws SQLException;
	
	public abstract Object getObject (ResultSet result_set,int index ) throws SQLException;
	
	public abstract int getDatabaseType ();
	
	public abstract void setFieldObject (Object anObject,PreparedStatement aStatement,int anIndex) throws SQLException;
	
	public void setObject (Object anObject,PreparedStatement aStatement,int anIndex);


}
