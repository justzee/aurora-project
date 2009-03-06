/*
 * Created on 2007-6-28
 */
package org.lwap.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureRunner;

/**
 * This class can be used by job scheduler to run a procedure at specified time
 * @author Zhou Fan
 *
 */
public class ProcedureInvoker {
    
    static Map proc_config_map = new HashMap();
    
    private UncertainEngine     engine;
    private Logger              logger;
    private WebApplication      app;
    
    public String       Procedure;
    public String       Config;
    
    /**
     * @param engine
     * @param logger
     */
    public ProcedureInvoker(UncertainEngine engine, Logger logger, WebApplication app) {
        super();
        this.engine = engine;
        this.logger = logger;
        this.app = app;
    }
    
    Procedure loadProcedure(String class_path)
        throws IOException
    {
        CompositeMap m = (CompositeMap)proc_config_map.get(class_path);
        if(m==null){
            m = engine.loadCompositeMap(class_path);
            if(m==null) throw new IOException("Can't load CompositeMap from "+class_path);
            proc_config_map.put(class_path, m);
        }
        Procedure proc = (Procedure) (engine.getOcManager().createObject(m));
        return proc;
    }    
    
    public void run() {
        ProcedureRunner runner = null;
        Procedure proc = null;
        Configuration config = null;
        CompositeMap map = null;
        try{
            map = app.getCompositeLoader().loadByFile(Config);
            config = engine.createConfig(map);
            proc = loadProcedure(Procedure);
            if(proc==null) throw new IllegalArgumentException("Can't load procedure "+Procedure);
            runner = engine.createProcedureRunner();
            runner.setProcedure(proc);
            runner.setContext(map);
            runner.setConfiguration(config);
            runner.run();
        }catch(Exception ex){
            ex.printStackTrace();
            logger.severe("error occured when running job: "+ex.getMessage());
        }finally{
            if(proc!=null)
                proc.clear();
            if(config!=null)
                config.clear();
            if(map!=null)
                map.clear();                
        }
    }

}
