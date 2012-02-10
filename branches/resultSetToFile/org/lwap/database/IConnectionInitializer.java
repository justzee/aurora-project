/*
 * Created on 2009-6-30
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;

import uncertain.composite.CompositeMap;

public interface IConnectionInitializer {
    
    public void initConnection( Connection conn, CompositeMap context ) throws SQLException;    

}
