/*
 * Created on 2008-11-5
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.lwap.controller.MainService;

import uncertain.proc.IExceptionHandle;
import uncertain.proc.ProcedureRunner;

public class ConnectionRollback implements IExceptionHandle {

    public boolean handleException(ProcedureRunner runner, Throwable exception) {
        Connection conn = (Connection)runner.getContext().get(MainService.KEY_CURRENT_CONNECTION);        
        if (conn != null) {
            try {
                //System.out.println("rolling back");
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

}
