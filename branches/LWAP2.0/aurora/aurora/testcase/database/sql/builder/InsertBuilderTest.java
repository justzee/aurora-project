/*
 * Created on 2008-5-23
 */
package aurora.testcase.database.sql.builder;

import junit.framework.TestCase;
import aurora.database.sql.InsertStatement;
import aurora.database.sql.builder.SqlBuilderRegistry;

public class InsertBuilderTest extends TestCase {
    
    SqlBuilderRegistry registry;
    InsertStatement    statement;

    public InsertBuilderTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new SqlBuilderRegistry();
        statement = new InsertStatement("EMP");
        
    }
    
    public static void assertContains(String s1, String s2){
        assertTrue(s1.indexOf(s2)>0);
    }
    
    public void testCreateUpdate(){
        statement.addInsertField("EMPNO", "EMP_S.nextval");
        statement.addInsertField("ENAME", "${@ENAME}");
        statement.addInsertField("MGR", "${@MGR}");
        statement.addInsertField("HIREDATE", "${@HIREDATE}");
        statement.addInsertField("DEPTNO", "${@DEPTNO}");
        statement.addInsertField("JOINDATE", "nvl(${@JOINDATE},sysdate)");
        
        String sql = registry.getSql(statement);
        assertContains(sql, "EMPNO,ENAME,MGR,HIREDATE,DEPTNO,JOINDATE");
        assertContains(sql, "EMP_S.nextval,${@ENAME},${@MGR},${@HIREDATE},${@DEPTNO},nvl(${@JOINDATE},sysdate)");
        System.out.println(sql);
        assertTrue(sql.indexOf("INSERT into EMP")==0);
    }

}
