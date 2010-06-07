/*
 * Created on 2006-9-14
 */
package org.lwap.database;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import uncertain.composite.CompositeComparator;
import uncertain.composite.CompositeMap;

public class PerformanceRecorder  {
    
    public static final String KEY_DATE_FROM = "DATE_FROM"; 
    public static final String KEY_DATE_TO = "DATE_TO";     
    public static final String KEY_SQL = "SQL";
    public static final String KEY_TOTAL_EXEC_TIME = "TOTAL_EXEC_TIME";
    public static final String KEY_TOTAL_EXEC_COUNT = "TOTAL_EXEC_COUNT"; 
    public static final String KEY_AVG_EXEC_TIME = "AVG_EXEC_TIME";
    public static final String KEY_MAX_EXEC_TIME = "MAX_EXEC_TIME";
    public static final String KEY_MIN_EXEC_TIME = "MIN_EXEC_TIME";
    public static final String KEY_AVG_EXEC_DATE = "EXEC_DATE";
    public static final String KEY_AVG_RECORD_DATE = "RECORD_DATE";    
    public static final String KEY_OWNER = "OWNER";
    public static final String KEY_EXEC_TIME = "EXEC_TIME";
    
    // sql -> SummaryRecord
    HashMap records = new HashMap();    
    boolean RecordDetail = false;
    CompositeMap model = null;
    
    public PerformanceRecorder(){
        
    }
   
    public class SummaryRecord {
        String          sql;
        HashSet         ownerSet = new HashSet();
        long            totalExecTime;
        long            totalExecCount;
        long            maxExecTime=0;
        long            minExecTime=9999999;
        LinkedList      execDetail = new LinkedList();
        Date            dateFrom = new Date();
        Date            dateTo;
        
        public SummaryRecord(String sql){
            this.sql = sql;
        }
        
        public void addRecord(DetailRecord r){
            totalExecCount++;
            totalExecTime+=r.execTime;
            if(minExecTime>r.execTime) minExecTime = r.execTime;
            if(maxExecTime<r.execTime) maxExecTime = r.execTime;
            ownerSet.add(r.owner);
            if(RecordDetail) execDetail.add(r);
            dateTo = new Date();            
        }
        
        
    }
    
    public static class DetailRecord {        
        Date            recordDate;
        String          owner;
        String          sql;
        long            execTime;
        /**
         * @param owner
         * @param sql
         * @param execTime
         */
        public DetailRecord(String owner, String sql, long execTime) {
            recordDate = new Date();
            this.owner = owner;
            this.sql = sql;
            this.execTime = execTime;
        }

    }
    
    public void addDetail(String owner, String sql, long execTime) {
        DetailRecord r = new DetailRecord(owner, sql, execTime);
        SummaryRecord s = (SummaryRecord)records.get(sql);
        if(s==null){
            s = new SummaryRecord(sql);
            records.put(sql, s);
        }
        s.addRecord(r);
    }    
    
    public void clear(){
        records.clear();
    }

    /**
     * @return the recordDetail
     */
    public boolean isRecordDetail() {
        return RecordDetail;
    }

    /**
     * @param recordDetail the recordDetail to set
     */
    public void setRecordDetail(boolean recordDetail) {
        RecordDetail = recordDetail;
    }
    
   
    public CompositeMap createModel(boolean sort){
        model = new CompositeMap();
        Iterator it = records.entrySet().iterator();
        int count=0;
        while(it.hasNext()){
            Map.Entry e = (Map.Entry)it.next();
            SummaryRecord r = (SummaryRecord)e.getValue();
            CompositeMap record = new CompositeMap("record");
            record.put(KEY_SQL, r.sql);
            record.put(KEY_TOTAL_EXEC_COUNT, new Long(r.totalExecCount));            
            record.put(KEY_TOTAL_EXEC_TIME, new Long(r.totalExecTime));
            record.put(KEY_MAX_EXEC_TIME, new Long(r.maxExecTime));
            record.put(KEY_MIN_EXEC_TIME, new Long(r.minExecTime));
            record.put(KEY_OWNER, r.ownerSet.toArray()[0]);
            record.put(KEY_AVG_EXEC_TIME, new Long(r.totalExecTime/r.totalExecCount));
            record.put(KEY_DATE_FROM, new Timestamp(r.dateFrom.getTime()));
            record.put(KEY_DATE_TO, new Timestamp(new Date().getTime()));            
            model.addChild(record);
            count++;
        }
        if( sort && count>0 )
            Collections.sort(model.getChilds(), new CompositeComparator(KEY_AVG_EXEC_TIME,false));
        return model;
    }
    
    public CompositeMap getModel( boolean sort ){
        if(model==null) createModel( sort );
        return model;
    }
    
    public CompositeMap getModel(){
        if(model==null) createModel( true );
        return model;        
    }

}
