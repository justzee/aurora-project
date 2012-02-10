/*
 * Created on 2007-3-19
 */
package org.lwap.feature;

import java.util.Calendar;
import java.util.Date;

import org.lwap.application.WebApplication;
import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTimestampField;

import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;

public class SystemTest implements IGlobalInstance {
    
    WebApplication app;
    UncertainEngine engine;
    
    public SystemTest(WebApplication app, UncertainEngine e){
        this.app = app;
        this.engine = e;
    }
    public void onInitialize() throws Exception {
        for (int i=0; i<100; i++){
            Thread t = new Thread(){
                
                public void run(){    
                    while(engine.isRunning()){
                        DatabaseTimestampField fld = (DatabaseTimestampField)DataTypeManager.getType(java.sql.Timestamp.class);
                        try{
                            int month = (int)(Math.random()*12);
                            if(month>10) month=9;
                            if(month==1) month=2;
                            Date ts = (Date)fld.parseString("2007-0"+month+"-13 12:12:00 ");
                            Calendar c = Calendar.getInstance();
                            c.setTime(ts);
                            int m = c.get(Calendar.MONTH);
                            if(m==0)
                                System.out.println(ts.toString()+" month:"+m);                   
                        } catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                    //System.out.println("finished");
                }
            };
            t.start();
        }
        System.out.println("done!");
    }
    
    public void onShutdown(){
        //System.out.println("shut down called");
    }
    
}
