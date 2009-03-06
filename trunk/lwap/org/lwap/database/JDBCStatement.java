/*
 * JDBCStatement.java
 *
 * Created on 2002年8月13日, 下午4:06
 */

package org.lwap.database;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import uncertain.composite.CompositeMap;
import uncertain.util.QuickTagParser;
import uncertain.util.TagParseHandle;

/**
 * Execute a JDBC statement, which may contain parameter access tags.
 * Parameters are stored in a CompositeMap
 * 
 * @author  zhoufan
 */
public class JDBCStatement {
	
	String                  parsed_sql = null;    
    //AdaptiveTagParser       parser   = AdaptiveTagParser.newUnixShellParser();
    QuickTagParser          parser   = new QuickTagParser(); 
    CompositeMap            context;
    long                    exec_time;
    
    /** Creates a new instance of JDBCStatement */
    public JDBCStatement(CompositeMap map) {
        context = map;
    }
/*
    void record(long prev_time){
        if(recorder==null) return;
        long execTime = System.currentTimeMillis()-prev_time;
        recorder.addDetail(null,parsed_sql,execTime);
    }
  */  
   class ParameterWrapper implements TagParseHandle {
       
     boolean             save_value = true;

     LinkedList          params;
     //CompositeMap   context;
     
     public  ParameterWrapper(){
        params = new LinkedList();
//        context = ct;
     }
     
     public ParameterWrapper(boolean _save_value){
         params = new LinkedList();
         save_value = _save_value;         
     }
       
     public String ProcessTag(int index, String tag) {
     	  if( tag.length()>0){
              // direct concatenate parsed value into sql string
     	  	  if( tag.charAt(0) == ':'){
     	  	  	  tag = tag.substring(1);
     	  	  	  Object obj = context.getObject(tag);
     	  	  	  if( obj != null) return obj.toString();
     	  	  	  else return "";
     	  	  } else {
                  // get value by tag
                  if(save_value){
    		          Object obj = context.getObject(tag);
    		          if( obj == null)
    		              return "null";
    		          else{          	
    		            params.addLast(obj);
    		            return "?";
    		          }
                   // save tag for later use
                  }else{
                      params.addLast(tag);
                      return "?";
                  }
     	  	  }
     	  } 
     	  else return "null";
     }
  	 
  	 public int ProcessCharacter( int index, char ch){
  	 	return (int)ch;
  	 }
     
     public LinkedList getParameters(){
           return params;
     }
     
     
     public void clear(){
        params.clear();
     }

   }  

   public String getParsedSql(){
       return parsed_sql;
   }
   
   
   public long getExecutionTime(){
       return exec_time;
   }

   
   void setParam( PreparedStatement ps, int index, Object obj) throws SQLException {
       DatabaseTypeField fld = DataTypeManager.getType( obj);
       if(fld==null) throw new IllegalArgumentException("Can't get registered data type for object:"+obj);
       fld.setFieldObject( obj, ps, index);
   }
   
    PreparedStatement  getStatement( Connection conn, String sql) throws SQLException { 
        
          ParameterWrapper  wrapper = new ParameterWrapper();
          parsed_sql = parser.parse( sql, wrapper);       
          PreparedStatement ps = conn.prepareStatement(parsed_sql);
        
          int id = 1;
          Iterator it = wrapper.getParameters().iterator();
          while( it.hasNext() )
              setParam( ps, id++, it.next());          
          return ps;          
    
    }
   /**
    * 
    * @param conn A database connection
    * @param sql sql string to execute
    * @param batch_source a list of CompositeMap as batch input parameter
    * @param allow_partial_failure if partial failure is allowed. If true, the invoke will be success 
    * @throws SQLException
    */ 
   public void executeBatchUpdate(Connection conn, String sql, List batch_source, boolean allow_partial_failure)
       throws SQLException
   {
       if(batch_source==null) return;
       if(batch_source.size()==0) return;
       int row = 1;
       
       ParameterWrapper  wrapper = new ParameterWrapper(false);
       PreparedStatement ps = null;
       
       try{
           String parsed_sql = parser.parse(sql, wrapper);
           ps = conn.prepareStatement(parsed_sql);
    
           Iterator it = batch_source.iterator();
           while(it.hasNext()){
               CompositeMap item = (CompositeMap) it.next();
               Iterator fit = wrapper.getParameters().iterator();
               int id=1;
               while(fit.hasNext()){
                   String path = (String)fit.next();
                   Object value = item.getObject(path);
                   if(value==null) ps.setNull(id, Types.VARCHAR);
                   else{
                       DatabaseTypeField fld = DataTypeManager.getType( value);
                       fld.setFieldObject(value,ps, id);
                   }
                   id++;
               }           
               ps.addBatch();        
           }
           ps.executeBatch();
       }catch(BatchUpdateException ex){
           int[] results = ex.getUpdateCounts();
           for(int i=0; i<results.length; i++){
               if (results[i] == Statement.EXECUTE_FAILED) {
                   CompositeMap item = (CompositeMap)batch_source.get(i);
               }
           }
           if(!allow_partial_failure) throw ex;
       }finally{
           
       }
   }
   
   public int executeUpdate( Connection conn, String sql) throws SQLException{
      PreparedStatement ps = null;
      exec_time = 0;
      try{  
        ps = getStatement( conn, sql);
        exec_time = System.currentTimeMillis();
        int result = ps.executeUpdate();
        exec_time = System.currentTimeMillis() - exec_time;
        return result;
        /*
   	  } catch(SQLException ex){
   	  	String msg =  ex.getMessage() + " origin sql statement:" + parsed_sql;
   	  	throw new SQLException( msg, ex.getSQLState(), ex.getErrorCode(), ex);
        */
   	  } finally {
   	    if(ps!=null)
   	        DBUtil.closeStatement(ps);
   	  }
   }
   /*
   public int executeBatchUpdate( Connection conn, String sql, Collection source) throws SQLException {
       ParameterWrapper  wrapper = new ParameterWrapper();
       parsed_sql = parser.parse( sql, wrapper);       
       PreparedStatement ps = conn.prepareStatement(parsed_sql);
       Iterator it = source.iterator();
       while(it.hasNext()){
           //CompositeMap m 
       }
       return 0;
   }
   */
   public ResultSet executeQuery( Connection conn, String sql) throws SQLException{
       exec_time = 0;
   	         
        PreparedStatement ps = getStatement( conn, sql);
        exec_time = System.currentTimeMillis();
        ResultSet rs = ps.executeQuery();
        exec_time = System.currentTimeMillis() - exec_time;
        return rs;
      /*
       try{     
      } catch(SQLException ex){
        String msg = ex.getMessage() + " origin sql statement:" + parsed_sql;
   	  	throw new SQLException( msg, ex.getSQLState(), ex.getErrorCode(), ex);        
   	  }
      */
   }   
   

   public static ResultSet executeQuery( Connection conn, String sql, CompositeMap context) throws SQLException{
       return new JDBCStatement(context).executeQuery(conn,sql);
   }
   
   public static int executeUpdate( Connection conn, String sql, CompositeMap context) throws SQLException{
       return new JDBCStatement(context).executeUpdate(conn,sql);
   }    
}
