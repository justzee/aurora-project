/*
 * Created on 2007-3-19
 */
package org.lwap.feature;

import javax.servlet.http.HttpServlet;

import org.lwap.application.FacadeServlet;

import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class EngineAdmin extends AbstractEntry {
    
    public String   Action = "";
    
    //UncertainEngine engine;
    FacadeServlet     servlet;
    
    public EngineAdmin(HttpServlet s){
        this.servlet = (FacadeServlet)s;
        //this.engine = engine;
    }
    
    public void run(ProcedureRunner runner){
        if("restart".equalsIgnoreCase(Action)){
            try{
                servlet.destroy();
                servlet.init();
            } catch(Exception ex){
                throw new RuntimeException("Error when restarting web application", ex);
            }
        }
    }

}
