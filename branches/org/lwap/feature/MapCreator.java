/*
 * Created on 2007-6-6
 */
package org.lwap.feature;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.lwap.database.DatabaseQuery;
import org.lwap.database.TransactionFactory;

import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;

public class MapCreator {
    
    TransactionFactory      tfact;
    UncertainEngine         engine;
    CompositeMap            result;
    Logger                  logger;
    
    
    /** SQL Statement to perform query that will return ResultSet*/
    public String       QuerySql;
    public String       KeyField;
    public String       ValueField;
    public String       MapPath;
    
    public MapCreator( TransactionFactory   tfact, UncertainEngine engine){
        this.tfact = tfact;
        this.engine = engine;
        this.logger = engine.getLogger();        
    }
    
    void assertNotNull(Object property, String message){
        if(property==null)
            logger.warning("[MapCreator] required field is null:"+message);
    }
    
    void validateConfig(){
        assertNotNull(QuerySql,"QuerySql");
        assertNotNull(MapPath, "MapPath");
        assertNotNull(KeyField, "KeyField");
        assertNotNull(ValueField, "ValueField");
    }
    
    public void onInitialize() throws SQLException {
        logger.info("[MapCreator] loading messages");
        int size=0;
        validateConfig();
        DatabaseQuery   query = DatabaseQuery.createQuery(QuerySql);
        CompositeMap result = tfact.query(query, null);
        CompositeMap map = new CompositeMap("map");
        Iterator it = result.getChildIterator();
        if(it!=null){
            while(it.hasNext()){
                CompositeMap item = (CompositeMap)it.next();
                Object key = item.get(KeyField);
                Object value = item.get(ValueField);
                map.put(key, value);  
                size++;
            }
            result.clear();       
            logger.info("[MapCreator] Total "+size+" records loaded into map '"+MapPath+"'");
        }else{
            logger.warning("[MapCreator] query return no result. sql:"+QuerySql);
        }
        engine.getGlobalContext().putObject(MapPath, map, true);
    }

}
