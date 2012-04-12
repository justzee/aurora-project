/*
 * Created on 2010-8-26 下午07:43:56
 * $Id$
 */
package aurora.testcase.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DummyDataSource implements DataSource {

    public Connection getConnection() throws SQLException {
        return new DummyConnection();
    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        return new DummyConnection();
    }

    public PrintWriter getLogWriter() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getLoginTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        // TODO Auto-generated method stub

    }

    public void setLoginTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub

    }

    public boolean isWrapperFor(Class arg0) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public Object unwrap(Class arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
