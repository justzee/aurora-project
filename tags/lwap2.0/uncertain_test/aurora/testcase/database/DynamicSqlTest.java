/*
 * Created on 2009-6-1
 */
package aurora.testcase.database;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.UncertainEngine;
import aurora.database.DynamicSqlParseHandle;
import aurora.database.SqlRunner;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;

public class DynamicSqlTest extends TestCase {
    
    DatabaseServiceFactory      factory;
    UncertainEngine         uncertainEngine;

    public DynamicSqlTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        uncertainEngine = new UncertainEngine();
        uncertainEngine.initialize(new CompositeMap());
        factory = new DatabaseServiceFactory(uncertainEngine);
    }
    
    public void testCreateDynamicSql()
        throws Exception
    {
        CompositeMap    map = new CompositeMap("root");
        SqlServiceContext context = (SqlServiceContext)DynamicObject.cast(map, SqlServiceContext.class);
        RawSqlService service = factory.getSqlService("aurora.testcase.database.DynamicQueryTest");
        CompositeMap parameter = context.getCurrentParameter();
        parameter.put("fields", "ename, decode(deptno, null, get_session(${@session_id}), deptno))");
        //parameter.put("table", "emp");
        parameter.put("table", "${:@table_name}");
        parameter.put("table_name", "emp");
        //parameter.put("ename", "${:@like_statement}");
        SqlRunner runner = service.createRunner(context);

        String  sql = runner.generateSQL(parameter);
        assertTrue(sql.indexOf("emp e")>=0);
        assertTrue(sql.indexOf("decode(deptno, null, get_session(?), deptno))")>=0);
        assertTrue(runner.getStatement().getBindParameters().size()==2);
        
        //System.out.println(runner.getStatement().getParsedSQL());
        
    }
    
    public void testUpdateSql()
        throws Exception
    {
        CompositeMap    map = new CompositeMap("root");
        SqlServiceContext context = (SqlServiceContext)DynamicObject.cast(map, SqlServiceContext.class);               
        RawSqlService service = factory.getSqlService("aurora.testcase.database.UpdateTest");
        SqlRunner runner = service.createRunner(context);
        runner.generateSQL(map);
        
        System.out.println(runner.getStatement().getParsedSQL());
    }
    
    public void testDynamicSqlHandle(){
        CompositeMap param = new CompositeMap();
        String sql = "select ename from emp where ${:@cond} and empno > ${@empno}";
        param.put("cond", "ename like ${@ename}");
        String result = DynamicSqlParseHandle.processSql(sql, param);
        assertEquals(result, "select ename from emp where ename like ${@ename} and empno > ${@empno}");
        
        param.put("cond", "${:@field}");
        param.put("field", "${:@myfield}");
        param.put("myfield", "(ename, empno) in (${@1}, ${@2})");
        result = DynamicSqlParseHandle.processSql(sql, param);
        assertEquals(result, "select ename from emp where (ename, empno) in (${@1}, ${@2}) and empno > ${@empno}");
    }

}
