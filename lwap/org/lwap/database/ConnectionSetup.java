/*
 * Created on 2008-11-9
 */
package org.lwap.database;

import java.sql.Connection;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;

public class ConnectionSetup implements ISingleton{
    
    public void addInitScript( CompositeMap init_script ){
        
    }
    
    public void onConnectionCreate( Connection conn ){
        
    }
    
    public void onConnectionClose( Connection conn ){
        
    }

}
