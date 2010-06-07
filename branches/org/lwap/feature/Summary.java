/*
 * Created on 2006-9-21
 */
package org.lwap.feature;

import java.util.logging.Logger;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

/**
 * 
 * <lwap:summary xmlns:lwap="org.lwap.feature" source="/model/M" field="@ROWNUM" target="/model/s/@value" function="sum"  />
 *
 */
public class Summary extends AbstractEntry {
    
    public String Source;
    public String Target;
    public String Field;
    public String Function;

    Logger  logger;
    
    public Summary(Logger l){
        logger = l;
    }
    
    public void run(ProcedureRunner runner) {        
        CompositeMap context = runner.getContext();
        CompositeMap src_part = (CompositeMap)context.getObject(Source);        
        if(src_part==null){
            logger.warning("can't get source model from["+Source+"]");
            return;
        }        
        Object o = CompositeUtil.groupResult(src_part, Field,Function);
        if(o==null){
            logger.warning("Can't get summary result from field ["+Field+"], function["+Function+"]");
        }
        if(Target==null){
            logger.warning("Target is null");
        }
        context.putObject(Target, o, true);
    }

}
