/**
 * Created on: 2002-11-13 19:34:01
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import uncertain.composite.CompositeMap;
import uncertain.composite.transform.Transformer;
import uncertain.logging.ILogger;

/**
 * 
 */
public class DatabaseQuery extends DatabaseAccess {
	
	public static final String KEY_TRANSFORM_LIST = "transform-list";

	public static final String KEY_ELEMENT_NAME = "ElementName";

	public static final String KEY_PAGE_RESULTSET = "PageResultset";
	public static final String KEY_PAGE_NUM = "PageNum";
	public static final String KEY_PAGE_SIZE = "PageSize";
	public static final String KEY_PAGENUM_PARAM_NAME = "PageNumParamName";
	public static final String KEY_PAGESIZE_PARAM_NAME = "PageSizeParamName";
    public static final String KEY_KEY_CASE = "KeyCase";
    

	public static final String DEFAULT_PAGENUM_PARAM = "@pagenum";
	public static final String DEFAULT_PAGESIZE_PARAM = "@pagesize";
	
		
	static long getLong(CompositeMap map, String param, long default_value){
		Object obj = map.getObject(param);
		if( obj == null) return default_value;
		if( obj instanceof Number) return ((Number)obj).longValue();
		return default_value;
	}
	
	public DatabaseQuery(){
		super();
	}
	
	public DatabaseQuery(String sql, String element_name){
		initialize();
		setSql(sql);
		getObjectContext().setName( DatabaseAccess.QUERY);
		setElementName(element_name);
	}
	
	public static DatabaseQuery createQuery(String sql){
		DatabaseQuery query = new DatabaseQuery(sql,null);
		return query;
	}
	
	public static DatabaseQuery createPagedQuery(String sql, String elm, long pagesize, long pagenum){
		DatabaseQuery query = new DatabaseQuery(sql,elm);
		query.setPageResultset(true);		
		query.setPageNum(pagenum);
		query.setPagesize(pagesize);
		return query;
	}
	
	public static DatabaseQuery createPagedQuery(String sql, String elm, String pagesize_param_name, String pagenum_param_name){
		DatabaseQuery query = new DatabaseQuery(sql,elm);
		query.setPageResultset(true);
		query.setPageNumParamName(pagenum_param_name);		
		query.setPagesizeParamName(pagesize_param_name);
		return query;
	}
	
	public static DatabaseQuery createPagedQuery(String sql, String elm){
		return createPagedQuery(sql,elm, DatabaseQuery.DEFAULT_PAGESIZE_PARAM, DatabaseQuery.DEFAULT_PAGENUM_PARAM );
	}


	public int getAccessType() {
		return DatabaseAccess.ACCESS_TYPE_QUERY;
	}

	public void execute(
		Connection conn,
		CompositeMap parameter,
		CompositeMap target)  throws SQLException
	{
		ResultSet rs = null;
		String	  _sql = getSql();
		initLogger( parameter );
		mLogger.log(Level.CONFIG, "Executing <query>");
		if( _sql == null) throw new IllegalArgumentException("'Sql' missing for query statement");
		
		JDBCStatement stmt = null;
		try{
            long execTime = System.currentTimeMillis();
			stmt = new JDBCStatement(parameter);
			stmt.setLogger(mLogger);
            rs = stmt.executeQuery(conn,_sql);
            rs.setFetchSize(50);
            String sql = stmt.getParsedSql();            
            mLogger.log(Level.CONFIG, sql);
			ResultSetLoader loader = new ResultSetLoader(rs);
            loader.setKeyCase(getKeyCase());
			// create target map if "Target" attribute is set
			CompositeMap target_map;
			String target_path = this.getTarget();
			if(target_path==null) 
				target_map = target;
			else{
			    target_map = (CompositeMap)target.getObject(target_path);
			    if(target_map==null)
			        target_map = target.createChildByTag(target_path);
			}
			boolean fetch_one = getBoolean("FetchOneRecord", false);
			if( fetch_one ){
			    if(rs.next())
			        loader.load(target_map, rs);
			}else{
    			if( isPageResultset()){
    				// Always get pagesize and pagenum from 'PageSize', 'PageNum' attribute first
    				// If not specified, see if can get pagesize & pagenum by alternaltive parameter name				
    				long page_size = this.getPagesize();
    				if( page_size<0) page_size = getLong(parameter, this.getPagesizeParamName(),1);
    
    				long page_num  = this.getPageNum();
    				if( page_num<0) page_num  = getLong(parameter, this.getPageNumParamName(), 1);
    				
    				mLogger.log(Level.CONFIG, "page_size="+page_size+" page_num="+page_num);
    				loader.loadList(target_map,this.getElementName(),rs,page_size*(page_num-1), page_size);
    			
    			}else{
    				// load whole ResultSet
    				loader.loadList(target_map,this.getElementName(),rs);
    			}
			}
			
            // record whole execution time, without transformation
            execTime = System.currentTimeMillis() - execTime;
            mLogger.log(Level.CONFIG, "Total execute time:"+execTime);
            super.recordTime(sql, execTime);
            
			CompositeMap transform_conf = getObjectContext().getChild(KEY_TRANSFORM_LIST);
			if( transform_conf != null) 
				target_map = Transformer.doBatchTransform(target_map,transform_conf.getChilds());
			
			/*
			if( getObjectContext().getString("Dump","").equalsIgnoreCase("true") && target_map != null) {
			    System.out.println("query sql:" + getSql());
				System.out.println("parameters:" + parameter.toXML());
				System.out.println(target_map.toXML());
			}
			*/
					
			
			 
		}catch(SQLException ex){
            dumpSql(ex,_sql);
            throw ex;
        }
        finally{
			if( rs != null) rs.close();
			if( stmt != null ) stmt.close();
		}
		
	}
	

	/**
	 * Returns the elementName.
	 * @return String
	 */
	public String getElementName() {
		return getString(KEY_ELEMENT_NAME,"record");
	}

	/**
	 * Sets the elementName.
	 * @param elementName The elementName to set
	 */
	public void setElementName(String elementName) {
		putString( DatabaseQuery.KEY_ELEMENT_NAME, elementName);
	}


	/**
	 * Returns the pageno.
	 * @return long
	 */
	public long getPageNum() {
		return getLong( DatabaseQuery.KEY_PAGE_NUM, -1);
	}

	/**
	 * Returns the pagenoParamName.
	 * @return String
	 */
	public String getPageNumParamName() {
		return getString( DatabaseQuery.KEY_PAGENUM_PARAM_NAME, DatabaseQuery.DEFAULT_PAGENUM_PARAM);
	}

	/**
	 * Returns the pageResultset.
	 * @return boolean
	 */
	public boolean isPageResultset() {
		return getBoolean( DatabaseQuery.KEY_PAGE_RESULTSET, false);
	}

	/**
	 * Returns the pagesize.
	 * @return long
	 */
	public long getPagesize() {
		return getLong( DatabaseQuery.KEY_PAGE_SIZE, -1);
	}

	/**
	 * Returns the pagesizeParamName.
	 * @return String
	 */
	public String getPagesizeParamName() {
		return getString(DatabaseQuery.KEY_PAGESIZE_PARAM_NAME, DEFAULT_PAGESIZE_PARAM);
	}
	
	/**
	 * Sets the pageno.
	 * @param pageno The pageno to set
	 */
	public void setPageNum(long pageno) {
		putLong(DatabaseQuery.KEY_PAGE_NUM, pageno);
	}

	/**
	 * Sets the pagenoParamName.
	 * @param pagenoParamName The pagenoParamName to set
	 */
	public void setPageNumParamName(String pagenoParamName) {
		putString(DatabaseQuery.KEY_PAGENUM_PARAM_NAME, pagenoParamName);
	}

	/**
	 * Sets the pageResultset.
	 * @param pageResultset The pageResultset to set
	 */
	public void setPageResultset(boolean pageResultset) {
		putBoolean(DatabaseQuery.KEY_PAGE_RESULTSET, pageResultset);
	}

	/**
	 * Sets the pagesize.
	 * @param pagesize The pagesize to set
	 */
	public void setPagesize(long pagesize) {
		putLong( DatabaseQuery.KEY_PAGE_SIZE, pagesize);
	}
    
    public void setKeyCase(String _case){
       putString(KEY_KEY_CASE, _case);
    }
    
    public String getKeyCase(){
        return getString(KEY_KEY_CASE);
    }

	/**
	 * Sets the pagesizeParamName.
	 * @param pagesizeParamName The pagesizeParamName to set
	 */
	public void setPagesizeParamName(String pagesizeParamName) {
		putString( DatabaseQuery.KEY_PAGESIZE_PARAM_NAME, pagesizeParamName );
	}
	
	public static void main(String[] args) throws Exception {

	   DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
       Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.11.242:1521:hrms","handhr","handhr");

	   String sql = "select employee_code,name,email from hr_lbr_employee l where (l.email like ${@email} or l.work_date<>${@work_date}) ";       
       CompositeMap result = new CompositeMap("result");
       CompositeMap params = new CompositeMap("param");
       params.put("email", "%s%");
       params.put("work_date", new java.sql.Date(new java.util.Date().getTime()) );
       params.put("pagenum", new Long(1));
       params.put("pagesize", new Long(50));
       
	   DatabaseQuery query = DatabaseQuery.createPagedQuery(sql,"employee");       
	   query.setTarget("/model/employee-list");
		System.out.println(query.getObjectContext().toXML());

	   query.execute(conn,params,result);
       System.out.println(result.toXML());
       conn.close();

	}

	

}
