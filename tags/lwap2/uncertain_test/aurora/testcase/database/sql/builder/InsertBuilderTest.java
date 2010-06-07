/*
 * Created on 2008-5-23
 */
package aurora.testcase.database.sql.builder;

import aurora.database.sql.InsertStatement;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectStatement;

public class InsertBuilderTest extends AbstractSqlBuilderTest {
    
    InsertStatement    statement;

    public InsertBuilderTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        statement = new InsertStatement("EMP");
        
    }

    
    public static InsertStatement createInsertSelect(){
            InsertStatement ins = new InsertStatement("EMP");
            SelectStatement sel = new SelectStatement(); 
            sel.addSelectSource("EMP");
            sel.addSelectField( new SelectField("EMP_S.NEXTVAL") );
            sel.addSelectField( new SelectField("ENAME"));
            sel.addSelectField( new SelectField("40"));
            sel.getWhereClause().addCondition("DEPTNO=30"); 
            ins.addInsertField("EMPNO", null);
            ins.addInsertField("ENAME", null);
            ins.addInsertField("DEPTNO", null);
            ins.setSelectStatement(sel);
            return ins;
    }
    
    public void testInsertSelect(){
        InsertStatement ins = createInsertSelect();
        String sql = mRegistry.getSql(ins);
        assertTrue(sql.indexOf("SELECT EMP_S.NEXTVAL,ENAME,40")>0);
        assertTrue(sql.indexOf("FROM EMP")>0);
        assertTrue(sql.indexOf("INSERT INTO EMP ( EMPNO,ENAME,DEPTNO)")==0);
    }
    
    public void testCreateUpdate(){
        statement.addInsertField("EMPNO", "EMP_S.nextval");
        statement.addInsertField("ENAME", "${@ENAME}");
        statement.addInsertField("MGR", "${@MGR}");
        statement.addInsertField("HIREDATE", "${@HIREDATE}");
        statement.addInsertField("DEPTNO", "${@DEPTNO}");
        statement.addInsertField("JOINDATE", "nvl(${@JOINDATE},sysdate)");
        
        String sql = mRegistry.getSql(statement);
        assertContains(sql, "EMPNO,ENAME,MGR,HIREDATE,DEPTNO,JOINDATE");
        assertContains(sql, "EMP_S.nextval,${@ENAME},${@MGR},${@HIREDATE},${@DEPTNO},nvl(${@JOINDATE},sysdate)");
        System.out.println(sql);
        assertTrue(sql.indexOf("INSERT INTO EMP")==0);
    }

}
