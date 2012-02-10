/*
 * Created on 2008-11-5
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.lwap.controller.MainService;

import uncertain.proc.IExceptionHandle;
import uncertain.proc.ProcedureRunner;

public class ConnectionRollback implements IExceptionHandle {
    
    public static void rollback( Connection conn ){
        try {
            //System.out.println("rolling back");
            conn.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }        
    }

    public boolean handleException(ProcedureRunner runner, Throwable exception) {
        // rollback connections
        Connection conn = (Connection)runner.getContext().get(MainService.KEY_CURRENT_CONNECTION);        
        if (conn != null) {
            rollback(conn);
        }
        // rollback connection set
        Map conn_map = (Map)runner.getContext().get(MainService.KEY_CONNECTION_SET);
        if(conn_map!=null){
            Collection c = conn_map.values();
            if(c!=null){
                Iterator it = c.iterator();
                while(it.hasNext()){
                    Connection conn1 = (Connection)it.next();
                    rollback(conn1);
                }
            }
        }
        return false;
    }

}
