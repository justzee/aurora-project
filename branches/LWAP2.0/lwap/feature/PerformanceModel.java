/*
 * Created on 2006-9-15
 */
package org.lwap.feature;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.lwap.database.CompositePersistent;
import org.lwap.database.PerformanceRecorder;

import uncertain.composite.CompositeMap;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

public class PerformanceModel extends AbstractEntry {
    
    public static final String SHOW = "show";
    public static final String SAVE = "save";    
    
    PerformanceRecorder     recorder;
    DataSource              data_source;
    
    public String Usage = SHOW;
    public String Table = "fnd_sql_stats";
    
    public PerformanceModel(PerformanceRecorder r, DataSource s){
        recorder = r;
        data_source = s;
    }
    
    public void save()throws SQLException{
         CompositeMap m = recorder.createModel(false);
         CompositePersistent p = new CompositePersistent(data_source);
         p.insert(m, Table);
         recorder.clear();
    }

    public void run(ProcedureRunner runner) throws Exception {  
        CompositeMap m = null;
        if(SHOW.equals(Usage)){
            m = recorder.createModel(true);
            CompositeMap model = runner.getContext().getChild("model");
            if(model==null) model = runner.getContext().createChild("model");
            m.setName("PERFORMANCE-LIST");
            model.addChild(m);
            System.out.println("performance show");
        }else if(SAVE.equals(Usage)){
            save();
        }
    }

}
