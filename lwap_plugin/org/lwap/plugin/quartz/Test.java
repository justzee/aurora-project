/*
 * Created on 2006-11-24
 */
package org.lwap.plugin.quartz;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.lwap.database.DBUtil;
import org.lwap.database.ResultSetLoader;

import uncertain.composite.CompositeMap;

/** A simple class that can be scheduled */

public class Test {
    
    public String Sql;
  
    /** Required to parameter, which can be provided by framework */
    public void runSql(DataSource ds, Logger logger )
        throws SQLException
    {
        logger.info("start running sql:"+Sql);

        Connection conn = null;
        ResultSet  rs = null;
        Statement  stmt = null;
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(Sql);
            ResultSetLoader loader = new ResultSetLoader(rs);
            CompositeMap result = loader.loadList(new CompositeMap("root"), "record", rs);
            logger.info("Finished loading:"+result.toXML());
        }finally{
            DBUtil.closeResultSet(rs);
            DBUtil.closeStatement(stmt);
            DBUtil.closeConnection(conn);
            
        }
    }    

}
