/*
 * Created on 2011-7-12 下午12:44:09
 * $Id$
 */
package aurora.database;

import java.sql.ResultSet;

public interface IResultSetProcessor {
    
    public void processResultSet( ResultSet rs );

}
