/*
 * Created on 2007-10-31
 */
package aurora.testcase.database;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

public class ConnectionProvider implements DataSource {
    
    String driver;
    String url;
    String user;
    String password;
    
    PrintWriter writer;
    
    public ConnectionProvider()
        throws Exception
    {
        Properties conf = new Properties();
        InputStream is = ConnectionProvider.class.getClassLoader().getResourceAsStream("aurora/testcase/database/db.conf");
        conf.load(is);
        
        driver = conf.getProperty("driver");
        url = conf.getProperty("url");
        user = conf.getProperty("user");
        password = conf.getProperty("password");
        
        
        DriverManager.registerDriver((Driver)(Class.forName(driver).newInstance()));        

    }
    
    public Connection getConnection()
        throws SQLException
    {
        Connection conn = DriverManager.getConnection(url,user,password);
        return conn;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return writer;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        this.writer = out;
        
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        return false;
    }

    public Object unwrap(Class arg0) throws SQLException {
        return this;
    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        return getConnection();
    }

}
