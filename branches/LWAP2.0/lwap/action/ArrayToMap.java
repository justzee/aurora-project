/*
 * Created on 2007-7-17
 */
package org.lwap.action;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.logging.Logger;

import oracle.sql.Datum;
import oracle.sql.STRUCT;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

/** Load a oracle struct into CompositeMap 
 *  <l:array-to-map xmlns:l="org.lwap.action" array_path="/model/@LIST" result_path="/model/emp_list"  />
 *
 */
public class ArrayToMap extends AbstractEntry {
    
    Logger  logger;
    
    public ArrayToMap(Logger l){
        this.logger = l;
    }
    
    public String Array_path;
    public String Result_path;
    public String Root = "array";

    public void run(ProcedureRunner runner) throws Exception {
        if(Array_path==null) throw new ConfigurationError("[ArrayToMap] Must specify 'array_path' attribute");
        if(Result_path==null) throw new ConfigurationError("[ArrayToMap] Must specify 'result_path' attribute");
        CompositeMap context = runner.getContext();
        Object obj = context.getObject(Array_path);
        if(obj==null){
            logger.warning("[ArrayToMap] Can't get array from "+Array_path);
            return;
        }
        // Get array from context
        Array array = null;
        try{
            array = (Array)obj;
        }catch(ClassCastException ex){
            throw new IllegalArgumentException("[ArrayToMap] Object retrieved from '"+Array_path+"' is not java.sql.Array, but of "+obj.getClass());
        }
        CompositeMap root = context.createChildByTag(Result_path);
        // load result set        
        ResultSet rs = array.getResultSet();
        ResultSetMetaData md = null;
        while(rs.next()){
            CompositeMap item = root.createChild("item");
            Object record = rs.getObject(2);
            if(record instanceof STRUCT){                
                STRUCT sr = (STRUCT)record;
                if(md==null)
                    md = sr.getDescriptor().getMetaData();
                if(md==null)
                    throw new IllegalStateException("Can't get meta data from oracle structure " + sr.getDescriptor().getName());
                Datum[] dts = sr.getOracleAttributes();
                for(int c=0; c<dts.length; c++){
                    if(dts[c]==null)
                        item.put(md.getColumnName(c+1),null);
                    else
                        item.put(md.getColumnName(c+1), dts[c].toJdbc());
                }
            }else{
                item.put("value", record);
            }
        }
    }

}
