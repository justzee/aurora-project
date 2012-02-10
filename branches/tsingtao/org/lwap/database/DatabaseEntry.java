/**
 * Adapter to invoke existing database access functions in procedure engine
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.lwap.application.BaseService;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class DatabaseEntry extends AbstractEntry implements IConfigurable {
    
    DatabaseAccess  da;
    Connection      conn;
    CompositeMap    parameter;
    CompositeMap    target;
    BaseService     service;
    boolean         construct_from_scratch = false;
    
    CompositeMap    config = null;
    
    public void beginConfigure(CompositeMap config){
        this.config = config;
    }
    
    public void endConfigure(){
    }
    
    public DatabaseEntry(){
    }
    
    public DatabaseEntry(
            DatabaseAccess da, 
            Connection conn, 
            CompositeMap parameter,
            CompositeMap target,
            BaseService  service)
    {
        this.da = da;
        this.conn = conn;
        this.parameter = parameter;
        this.target = target;
        this.service = service;
        da.setService(service);
    }
    
    public void run(ProcedureRunner runner) {
            if(service==null) construct_from_scratch = true;
            if(construct_from_scratch){
                CompositeMap context = runner.getContext();
                service = MainService.getServiceInstance(context.getRoot());
                conn = MainService.getConnection(context.getRoot());
                parameter = runner.getContext();
                target = runner.getContext();
                da = DatabaseAccess.getInstance(config);            
            }
            try{
                da.execute(conn, parameter, target);
            }catch(SQLException ex){
                //ex.printStackTrace();
                runner.throwException(ex);
            }
    }

}
