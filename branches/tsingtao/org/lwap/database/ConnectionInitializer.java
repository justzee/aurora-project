/*
 * Created on 2009-6-30
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;

import uncertain.composite.CompositeMap;

public class ConnectionInitializer implements IConnectionInitializer {

    String          mSql;
    
    /**
     * @param sql
     */
    public ConnectionInitializer(String sql) {
        super();
        mSql = sql;
    }
    
    public void initConnection( Connection conn, CompositeMap context ) throws SQLException {
        JDBCStatement stmt = new JDBCStatement( context );
        stmt.executeUpdate(conn, mSql);
    }

}
