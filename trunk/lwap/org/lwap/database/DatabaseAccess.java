/**
 * Created on: 2002-11-13 18:15:13
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwap.application.BaseService;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * <database-access>
 * 
 * <query   Sql             ="sql_statement" 
 * 			ElementName     ="name_for_each_record"
 * 			[Target         ="composite_accessor_for_query_target"]
 * 			PageResultset   ="true|false"
 * 			[PageNum		   = "page_no"]
 * 			[PageSize		   = "page_size"] />
 * 			[PageNumParamName  = "composite_accessor_for_page_no"]
 * 			[PageSizeParamName = "composite_accessor_for_page_size"] />
 * 
 * <update  Sql				="sql_statement"
 * 			[Target         ="composite_accessor_for_update_result"] 
 * 			[Batch			="true|false"]
 * 			/>
 * 
 * <batch BatchSource   = "composite_access_tag"
 *        [
 *         ParameterName = "tagged string for parameter name" 
 *         TargetName    = "where to create attribute for parsed parameter"         
 *         DataType      = "java.lang.String" 
 *         Nullable="true"
 *        ] >
 *    <sub-statements/>
 * </batch>
 * 
 * <bundle>
 *    <other database access statements />
 * </bundle> 
 * 
 * 
 * </database-access>
 */
public abstract class DatabaseAccess extends DynamicObject {
	
	BaseService	               service;
    PerformanceRecorder        recorder;
    String                     owner;
    
    public static void dumpSql( SQLException ex, String sql ){
        System.err.println("============ Error when executing SQL ===================");
        System.err.println(new Date());
        System.err.println(ex.getMessage());
        System.err.println(sql);
    }
	
	public void setService( BaseService s){
		service = s;
	}
    
    public void setPerformanceRecorder(PerformanceRecorder p){
        recorder = p;
    }
    
    public void setOwner(String o){
        owner = o;
    }
	
	public BaseService getService(){
		return service;
	}
    
    public void recordTime( String sql, long execTime){
        if(recorder!=null)
            recorder.addDetail(owner, sql, execTime);
    }
	
	public static final String UPDATE 				= "update";
	public static final String QUERY 				= "query";
    public static final String QUERY_STATEMENT      = "query-statement";    
	public static final String STORED_PROCEDURE 		= "procedure";
	public static final String BATCH 				= "batch";
	public static final String BUNDLE 				= "bundle";
	public static final String BATCH_PARAMETER		= "batch-parameter";	
	
	public static final int ACCESS_TYPE_QUERY            = 0;
	public static final int ACCESS_TYPE_UPDATE           = 1;
	public static final int ACCESS_TYPE_STORED_PROCEDURE = 2;
	public static final int ACCESS_TYPE_BATCH            = 3;
	public static final int ACCESS_TYPE_BUNDLE           = 4;
	public static final int ACCESS_TYPE_BATCH_PARAMETER  = 5;

	
	public static final String KEY_SQL = "Sql";
	public static final String KEY_TARGET = "Target";
	
	public abstract int getAccessType();
	
	public abstract void execute( Connection conn, CompositeMap parameter, CompositeMap target) throws SQLException;
	

	static Map type_mapping = new HashMap(10);
	
	static {
		type_mapping.put(UPDATE, new Integer(ACCESS_TYPE_UPDATE) );
		type_mapping.put(QUERY, new Integer(ACCESS_TYPE_QUERY) );
        //type_mapping.put(QUERY_STATEMENT, new Integer(ACCESS_TYPE_QUERY));
		type_mapping.put(STORED_PROCEDURE, new Integer(ACCESS_TYPE_STORED_PROCEDURE) );		
		type_mapping.put(BATCH,new Integer(ACCESS_TYPE_BATCH));
		type_mapping.put(BUNDLE,new Integer(ACCESS_TYPE_BUNDLE));
		type_mapping.put(BATCH_PARAMETER,new Integer(ACCESS_TYPE_BATCH_PARAMETER));
	}
	
	public static int getAccessType( String type_name){
		Integer it = (Integer)type_mapping.get(type_name);
		return it==null? -1:it.intValue();
	}
	
	public static DatabaseAccess getInstance( CompositeMap map){
		DatabaseAccess dba = null;
		int type = getAccessType( map.getName());
		switch(type){
			case ACCESS_TYPE_QUERY:
					dba = new DatabaseQuery();
//					System.out.println(map.toXML());
					break;
			case ACCESS_TYPE_UPDATE:
					dba = new DatabaseUpdate();
					break;	
			case ACCESS_TYPE_STORED_PROCEDURE:
					dba = new DatabaseProcedure();
					break;	
			case ACCESS_TYPE_BATCH:
					dba = new DatabaseBatch();
					break;	
			case ACCESS_TYPE_BUNDLE:
					dba = new DatabaseBundle();
					break;
			case ACCESS_TYPE_BATCH_PARAMETER:
					dba = new BatchParameter();
					break;				
		}
		
		if( dba == null) return null;
		else{
			dba.initialize(map);
			return dba;
		}
	}
	
	public static void execute( Collection   access_def_list, 
								  Connection   conn, 	
								  CompositeMap parameter,
								  CompositeMap target) throws SQLException
	{
		//assert access_def_list != null;
		if(access_def_list == null) return;		
		Iterator it = 	access_def_list.iterator();
		while( it.hasNext()){
			CompositeMap node = (CompositeMap)it.next();
//			System.out.println(node.toXML());
			DatabaseAccess dba = getInstance(node);
			if( dba != null) dba.execute(conn,parameter,target);
		}						  
	}
	

	public static void execute( 
								  BaseService  service,
								  Collection   access_def_list, 
								  Connection   conn, 	
								  CompositeMap parameter,
								  CompositeMap target) throws SQLException
	{
		//assert access_def_list != null;
		if(access_def_list == null) return;		
		Iterator it = 	access_def_list.iterator();
		while( it.hasNext()){
			CompositeMap node = (CompositeMap)it.next();
//			System.out.println(node.toXML());
			DatabaseAccess dba = getInstance(node);			
			if( dba != null){ 
				dba.setService(service);
				dba.execute(conn,parameter,target);
			}
		}						  
	}
	
	/**
	 * Returns the sql.
	 * @return String
	 */
	public String getSql() {
	    String sql = getString(DatabaseAccess.KEY_SQL);
	    if(sql==null) sql = getObjectContext().getText();
	    return sql;
	}
	
	/**
	 * Sets the sql.
	 * @param sql The sql to set
	 */
	public void setSql(String sql) {
		putString( DatabaseAccess.KEY_SQL, sql);
	}
	

	/**
	 * Returns the target.
	 * @return String
	 */
	public String getTarget() {
		return getString( DatabaseAccess.KEY_TARGET);
	}


	/**
	 * Sets the target.
	 * @param target The target to set
	 */
	public void setTarget(String target) {
		putString( DatabaseAccess.KEY_TARGET, target);
	}
	
	public void setAccessType(String type){
		this.getObjectContext().setName(type);
	}

}
