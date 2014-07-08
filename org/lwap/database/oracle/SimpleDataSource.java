/*
 * Created on 2008-11-14
 */
package org.lwap.database.oracle;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SimpleDataSource implements DataSource {
    
    PrintWriter     logger;
    int             loginTimeOut;
    
    String          Url;
    String          UserName;
    String          Password;

    /**
     * @param url
     * @param userName
     * @param password
     */
    public SimpleDataSource(String url, String userName, String password) 
    
    {
        try{
            Url = url;
            UserName = userName;
            Password = password;
            Class.forName("oracle.jdbc.driver.OracleDriver");
            DriverManager.registerDriver( new oracle.jdbc.driver.OracleDriver());
        }catch(Exception ex){
            throw new RuntimeException("Can't register oracle driver",ex);
        }
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return Password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        Password = password;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return Url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        Url = url;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        UserName = userName;
    }

    public Connection getConnection() throws SQLException {        
        Connection conn = DriverManager.getConnection(
                Url, UserName, Password);
        //System.out.println("Generating connection "+conn);
        return conn;

    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return logger;
    }

    public int getLoginTimeout() throws SQLException {        
        return loginTimeOut;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logger = out;

    }

    public void setLoginTimeout(int seconds) throws SQLException {
        this.loginTimeOut = seconds;

    }
    
    public boolean isWrapperFor(Class t){
        return false;
    }
    
    public Object unwrap(Class t) throws SQLException{
        return null;
    }

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
