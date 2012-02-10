/*
 * Created on 2007-10-31
 */
package aurora.testcase.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {
    
    String driver;
    String url;
    String user;
    String password;
    
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

}
