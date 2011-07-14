/*
 * Created on 2011-7-12 下午04:39:11
 * $Id$
 */
package org.lwap.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.lwap.database.DBUtil;

import aurora.service.IResourceReleaser;
import aurora.service.ServiceContext;

public class ConnectionCommiter implements IResourceReleaser {
    
    Connection      conn;
    MainService     service;

    /**
     * @param conn
     * @param service
     */
    public ConnectionCommiter(Connection conn, MainService service) {
        super();
        this.conn = conn;
        this.service = service;
    }

    public void doRelease(ServiceContext context) {    	
        try{
        	if(conn!=null&&!conn.isClosed())
        		conn.commit();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        DBUtil.closeConnection(conn);    	
    }
    


}
