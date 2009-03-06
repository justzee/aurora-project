/**
 * Created on: 2003-2-28 14:24:19
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class DatabaseBundle extends DatabaseAccess {

	/**
	 * @see org.lwap.database.DatabaseAccess#getAccessType()
	 */
	public int getAccessType() {
		return DatabaseAccess.ACCESS_TYPE_BUNDLE;
	}

	/**
	 * @see org.lwap.database.DatabaseAccess#execute(Connection, CompositeMap, CompositeMap)
	 */
	public void execute(
		Connection conn,
		CompositeMap parameter,
		CompositeMap target)
		throws SQLException {
			
			//System.out.println("Bundle called");
			Iterator it = this.getObjectContext().getChildIterator();
			if( it == null) return;
			while( it.hasNext()){
				
				DatabaseAccess da = DatabaseAccess.getInstance((CompositeMap)it.next());
				if( da != null) da.execute(conn,parameter,target);
				
			}
			
	}

}
