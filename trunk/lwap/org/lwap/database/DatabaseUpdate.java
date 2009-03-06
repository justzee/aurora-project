/**
 * Created on: 2002-11-13 19:39:46
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class DatabaseUpdate extends DatabaseAccess {
	
	public static final String KEY_DEFAULT_RESULT = "result";
	
	public static DatabaseUpdate createUpdate( String sql){
		return new DatabaseUpdate(sql);
	}
	
	public DatabaseUpdate(){
		super();
	}
	
	public DatabaseUpdate( String sql, String target){		
		initialize();
		setSql(sql);
		setTarget(target);
		getObjectContext().setName(DatabaseAccess.UPDATE);
	}
	
	public DatabaseUpdate( String sql){
		this(sql, KEY_DEFAULT_RESULT);
	}
	

	public int getAccessType() {
		return DatabaseAccess.ACCESS_TYPE_UPDATE;
	}


	public void execute(
		Connection conn,
		CompositeMap parameter,
		CompositeMap target)  throws SQLException
	{
		String	  _sql = getSql();
		if( _sql == null) throw new IllegalArgumentException("'Sql' missing for update statement");
        boolean dump = false;
        Boolean d = getBoolean("Dump");
        if(d!=null)
            dump = d.booleanValue();
        if( dump){
            System.out.println("Update sql:"+_sql);
            System.out.println("Parameter:"+parameter.toXML());
        }
		try{
            JDBCStatement js = new JDBCStatement(parameter);
    		long n = js.executeUpdate(conn, _sql);
            super.recordTime(js.getParsedSql(), js.getExecutionTime());
    		String target_key = getTarget();		
    		if( target_key == null) target_key = KEY_DEFAULT_RESULT;
    		target.putObject(target_key,new Long(n), true);
            if( dump ){
                System.out.println("Update finished, "+n+" records affected");
            }
        }catch(SQLException ex){
            dumpSql(ex, _sql);
            throw ex;
        }
	}
	
	public static void main(String[] args) throws Exception {

	   DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
       Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@hr:1521:hrms","scott","tiger");
       
       CompositeMap result = new CompositeMap("result");
       CompositeMap params = new CompositeMap("param");
       params.put("email", "seacat.zhou@hand-china.com");
       params.put("work_date", new java.sql.Date(new java.util.Date().getTime()) );
       params.put("employee_code", "109");       
       String sql = "update hr_lbr_employee l set l.email = ${@email}, l.work_date = ${@work_date}  where l.employee_code = ${@employee_code}"; 
       
       DatabaseUpdate dbu = new DatabaseUpdate(sql, null);
       dbu.execute(conn,params,result);
       System.out.println(result.toXML());
       conn.commit();
       conn.close();

	}

}
