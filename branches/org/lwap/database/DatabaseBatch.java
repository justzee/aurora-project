/**
 * Created on: 2002-12-25 15:27:52
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.logging.ILogger;

/**
 * <batch BatchSource="/path/to/source" 
 *        AllowPartialFailure="true" 
 *        SuccessFlagPath="/path/to/success/flag" 
 *        FailedRecordPath="/path/to/put/failed_record" 
 *        ExceptionHandlePath="/path/to/instanceof/IBatchExceptionHandle" />
 */
public class DatabaseBatch extends DatabaseAccess implements IBatchExceptionHandle {
	
	public static final String KEY_BATCH_SOURCE             = "BatchSource" ;
    public static final String KEY_ALLOW_PARTIAL_FAILURE    = "AllowPartialFailure";
    public static final String KEY_SUCCESS_FLAG_PATH        = "SuccessFlagPath";
    public static final String KEY_FAILED_RECORD_PATH       = "FailedRecordPath";
    public static final String KEY_EXCEPTION_HANDLE_PATH    = "ExceptionHandlePath";
    
    public static final String KEY_ROWNUM = "_rownum";
    public static final String KEY_EXCEPTION = "_exception";
    
    /*
	public static final String KEY_PARAMETER_NAME = "ParameterName";		
	public static final String KEY_TARGET_NAME    = "TargetName";	
    */	
    
    CompositeMap            failed_target;
    IBatchExceptionHandle   handle;
    boolean                 is_success = true;

    public boolean getAllowPartialFailure(){
        return getBoolean(KEY_ALLOW_PARTIAL_FAILURE, false);
    }
    
    public void setAllowPartialFailure(boolean b){
        putBoolean(KEY_ALLOW_PARTIAL_FAILURE, b);
    }
    
    public String getSuccessFlagPath(){
        return getString(KEY_SUCCESS_FLAG_PATH);
    }
    
    public void setSuccessFlagPath(String path){
        putString(KEY_SUCCESS_FLAG_PATH, path);
    }
    
    public String getFailedRecordPath(){
        return getString(KEY_FAILED_RECORD_PATH);
    }
    
    public void setFailedRecordPath(String path){
        putString(KEY_FAILED_RECORD_PATH, path);
    }
    
    public String getBatchSource(){
        return getString(KEY_BATCH_SOURCE);
    }
    
    public void setBatchSource(String source){
        putString(KEY_BATCH_SOURCE, source);
    }
    
    public void batchBegin(){
        String path = getFailedRecordPath();
        failed_target = new CompositeMap("result");
    }
    
    public void batchEnd(){
        String path = getSuccessFlagPath();
        if(path!=null) getObjectContext().putObject(path, new Boolean(is_success), true);
    }

    /** default behavior for batch exception */
    public boolean handleException(int row, CompositeMap param, SQLException exp ){
        if(failed_target!=null){ 
            param.put(KEY_EXCEPTION,exp.getMessage());
            param.put(KEY_ROWNUM, new Integer(row));
            failed_target.addChild(param);
        }
         return getAllowPartialFailure();
    }
    
    /**
	 * @see org.lwap.database.DatabaseAccess#getAccessType()
	 */
	public int getAccessType() {
		return DatabaseAccess.ACCESS_TYPE_BATCH;
	}

	/**
	 * @see org.lwap.database.DatabaseAccess#execute(Connection, CompositeMap, CompositeMap)
	 */
	public void execute(
		Connection conn,
		CompositeMap parameter,
		CompositeMap target)
		throws SQLException {
	        initLogger(parameter);
	        mLogger.log(Level.CONFIG, "Executing <batch>");
	        
			Collection sub_stmts = getObjectContext().getChilds();
			if( sub_stmts == null){
			    mLogger.log(Level.CONFIG, "No sub statement defined, exiting");
			    return;
			}
            
            /*
			String param_name  = getObjectContext().getString(KEY_PARAMETER_NAME);
			String target_name = getObjectContext().getString(KEY_TARGET_NAME);
			*/
            
            //Get batch exception handle, if null, set to this
            handle=this;
            String handle_path = getString(KEY_EXCEPTION_HANDLE_PATH);
            if(handle_path!=null){
                Object obj = getObjectContext().getObject(handle_path);
                if(obj!=null) handle = (IBatchExceptionHandle)obj;
            }
            
            Collection cl = parameter.getChilds();
			String batch_source = getString(KEY_BATCH_SOURCE);
			if( batch_source != null){ 
				CompositeMap batch = (CompositeMap)parameter.getObject(batch_source);
				if( batch != null) cl = batch.getChilds();
			}
			// modified to enable composite batch
			if( cl == null) {
			    if(parameter!=null) cl = parameter.getChilds();
			}
			// end modify
			if( cl == null) return;
			handle.batchBegin();
            int row=0;
			Iterator it = cl.iterator();
			while( it.hasNext()){
			    mLogger.log(Level.CONFIG, "batch record No."+row);
				CompositeMap  child_param = (CompositeMap) it.next();
				try{
                    DatabaseAccess.execute(sub_stmts, conn, child_param, child_param);
                }catch(SQLException ex){
                    is_success = false;
                    if(!handle.handleException(row, child_param, ex)){
                        if(failed_target!=null)parameter.putObject(getFailedRecordPath(), failed_target, true);
                        throw ex;
                    }
                }
                row++;
			}
			mLogger.log(Level.CONFIG, "Total "+row+" records in batch");
            if(failed_target!=null)parameter.putObject(getFailedRecordPath(), failed_target, true);
            String path = getSuccessFlagPath();
            if(path!=null) parameter.putObject(path, new Boolean(is_success), true);
            handle.batchEnd();
			
	}

}
