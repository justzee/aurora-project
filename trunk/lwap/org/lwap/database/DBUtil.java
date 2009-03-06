/**
 * Created on: 2003-9-11 10:36:38
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *  quietly close jdbc connection, statement, etc
 */
public class DBUtil {
	
	public static void closeConnection( Connection conn ){
		if( conn == null) return;
		try{
			conn.close();
		} catch(SQLException ex){
			
		}
	}
	
	public static void closeResultSet( ResultSet rs ){
		if( rs == null) return;
		try{
			rs.close();
		} catch(SQLException ex){
			
		}
	}
	
	public  static void closeStatement( Statement stmt ){
		if( stmt == null) return;
		try{
			stmt.close();
		} catch(SQLException ex){
			
		}
	}
    

}
