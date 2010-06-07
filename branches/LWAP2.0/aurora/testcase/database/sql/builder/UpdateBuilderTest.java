/*
 * Created on 2008-5-23
 */
package aurora.testcase.database.sql.builder;

import junit.framework.TestCase;
import aurora.database.sql.BaseField;
import aurora.database.sql.CompareExpression;
import aurora.database.sql.ConditionList;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.UpdateStatement;
import aurora.database.sql.UpdateTarget;
import aurora.database.sql.builder.SqlBuilderRegistry;

public class UpdateBuilderTest extends TestCase {
    
    SqlBuilderRegistry registry;
    UpdateStatement    statement;

    public UpdateBuilderTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new SqlBuilderRegistry();
        statement = new UpdateStatement("EMP");
        
    }
    
    public static void assertContains(String s1, String s2){
        assertTrue(s1.indexOf(s2)>0);
    }
    
    public void testCreateUpdate(){
        UpdateTarget target = statement.getUpdateTarget();
        statement.addUpdateField("ENAME", "${@ENAME}");
        statement.addUpdateField("MGR", "${@MGR}");
        statement.addUpdateField("HIREDATE", "${@HIREDATE}");
        statement.addUpdateField("DEPTNO", "${@DEPTNO}");
        statement.addUpdateField("JOINDATE", "nvl(${@JOINDATE},sysdate)");
        
        String sql = registry.getSql(statement);
        assertContains(sql, "t.ENAME=${@ENAME},");
        assertContains(sql, "t.MGR=${@MGR},");
        assertContains(sql, "t.HIREDATE=${@HIREDATE},");
        assertTrue(sql.indexOf("WHERE")<0);
        
        ConditionList where = statement.getWhereClause();
        BaseField pk = target.createField("EMPNO");
        where.addEqualExpression(pk, new RawSqlExpression("${@EMPNO}"));
        BaseField fld = target.getField("DEPTNO");
        CompareExpression exp = new CompareExpression(fld, CompareExpression.IS_NOT_NULL, null);
        where.addCondition(exp);
        
        sql = registry.getSql(statement);
        assertContains(sql, "WHERE");
        assertContains(sql, "t.EMPNO = ${@EMPNO}");
        assertContains(sql, "t.DEPTNO IS NOT NULL");
        
        System.out.println(sql);
        
    }

}
