/*
 * Created on 2007-10-31
 */
package aurora.testcase.database;

import junit.framework.TestCase;
import aurora.database.ParsedSql;

public class SQLStatementTest extends TestCase {
    
    static final String[] paths = {"@fields", "@order_id", "/preference/@status", "@condition"};
    
    public SQLStatementTest(String name){
        super(name);
    }
    
    /*
    public void testParseSQL(){
        String sql = "select ${!@fields} from order_list where order_id = ${@order_id} and order_status = ${/preference/@status} where ${!@condition}";
        SQLStatement stmt = new SQLStatement(sql);
        List params = stmt.getBindParameters();
        assertEquals(params.size(), 4);
        for(int i=0; i<4; i++){
            BindParameter param = (BindParameter)params.get(i);
            assertEquals(param.getPath(), paths[i]);
            if(i==0 || i==3) assertTrue(param.isSQLStatement());
            else assertTrue(!param.isSQLStatement());
        }

        SQLExecutor exec = new SQLExecutor();
        CompositeMap param = new CompositeMap();
        param.put("fields", "order_line, order_no, ship_date");
        param.put("condition", "ship_date>sysdate");
        exec.setStatement(stmt);
        String s = exec.generateSQL(param);
        assertEquals(s, "select order_line, order_no, ship_date from order_list where order_id = ? and order_status = ? where ship_date>sysdate");
    }
    */
    
    public void testParseSQL2(){
        String sql = "select sysdate from dual";
        ParsedSql stmt = new ParsedSql(sql);
        assertEquals(stmt.getParsedSQL(), sql);
        assertEquals(stmt.getBindParameters().size(), 0);
    }
    

    /*
    public void testUpdate() throws Exception {
        ConnectionProvider provider = new ConnectionProvider();
        Connection conn = null;
        try{
            conn = provider.getConnection();
            String sql = "update emp set sal = ${@sal} where ename = ${@name}";
            SQLStatement stmt = new SQLStatement(sql);
            CompositeMap params = new CompositeMap();
            params.put("sal", new Double(780.5));
            params.put("name", "SMITH");
            SQLExecutor exec = new SQLExecutor(conn, stmt);
            int rows = exec.update(params);
            assertEquals(rows,1);
            conn.commit();
        }finally{
            DBUtil.closeConnection(conn);
        }
        
    }
    
    
    public void testFunctionCall() throws Exception{
        ConnectionProvider provider = new ConnectionProvider();
        Connection conn = null;
        SQLExecutor exec = null;
        CompositeMap params = new CompositeMap();
        try{
            conn = provider.getConnection();
            String sql = "{ ${@result} = call is_manager_valid( ${@empno}, ${@mgr} ) }";
            
            SQLStatement stmt = new SQLStatement();
            Parameter p = new Parameter();
            p.setName("result");
            p.setDataType("java.lang.String");
            p.setOutput(true);
            p.setInput(false);
            stmt.defineParameter(p);
            

            stmt.parse(sql);
            assertTrue(stmt.hasOutputParameter());
            
            
            params.put("empno", new Long(7369));
            params.put("mgr", new Long(7369));
            exec = new SQLExecutor(conn, stmt);
            
            int rows = exec.update(params);
            assertEquals(params.getString("result"), "0");

            
            params.clear();
            params.put("name", "TEST");
            sql =   "begin " +
                    "delete from emp where empno = '1000';" +
                    "insert into emp(empno, ename) values (1000, ${@name}) returning creation_date into ${@result}; " +
                    "end;";
            stmt.parse(sql);
            assertTrue(stmt.hasOutputParameter());
            exec = new SQLExecutor(conn, stmt);
            exec.update(params);
            assertNotNull(params.get("result"));
            //System.out.println(params.toXML());
            
            //conn.commit();
        }finally{
            DBUtil.closeConnection(conn);
        }               
        
    }
    */

}
