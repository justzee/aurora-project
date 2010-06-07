/*
 * Created on 2008-4-28
 */
package aurora.testcase.database.sql.builder;

import junit.framework.TestCase;
import aurora.database.sql.CompareExpression;
import aurora.database.sql.Condition;
import aurora.database.sql.ILogicalExpression;
import aurora.database.sql.Join;
import aurora.database.sql.OrderByField;
import aurora.database.sql.RawSqlExpression;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectSource;
import aurora.database.sql.SelectStatement;
import aurora.database.sql.builder.SqlBuilderRegistry;

public class SelectBuilderTest extends TestCase {
    
    SqlBuilderRegistry      registry;
    SelectSource            emp;
    SelectSource            dept;
    SelectStatement         dept_list_query;
    SelectStatement         emp_list_query;

    public SelectBuilderTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        emp = new SelectSource("EMP");
        dept_list_query = new SelectStatement();
        SelectSource s1 = new SelectSource("DEPT");
        dept_list_query.addSelectSource(s1);
        dept_list_query.addSelectField(s1.createSelectField("*"));        
        dept = new SelectSource(dept_list_query);
        SelectField e1 = emp.createSelectField("DEPTNO"), e2 = dept.createSelectField("DEPTNO");
        Join j1 = new Join(Join.TYPE_LEFT_OUTTER_JOIN, emp, dept);
        CompareExpression c1 = new CompareExpression(e1, CompareExpression.EQUAL, e2);
        j1.addJoinCondition(c1);
        emp_list_query = new SelectStatement();
        emp_list_query.addSelectSource(emp);
        emp_list_query.addSelectSource(dept);
        emp_list_query.addJoin(j1);
        emp_list_query.addSelectField(emp.createSelectField("EMPNO"));
        emp_list_query.addSelectField(emp.createSelectField("ENAME", "EMPLOYEE_NAME"));
        emp_list_query.addSelectField(emp.createSelectField("HIREDATE"));
        emp_list_query.addSelectField(emp.createSelectField("JOB"));
        emp_list_query.addSelectField(emp.createSelectField("MGR"));
        emp_list_query.addSelectField(dept.createSelectField("DNAME"));
        emp_list_query.addSelectField(dept.createSelectField("LOC"));
        emp_list_query.addSelectField(new SelectField("sysdate"));
        
        CompareExpression c2 = new CompareExpression(emp.createSelectField("SAL"), CompareExpression.GREATER_THAN, new RawSqlExpression("2000"));
        ILogicalExpression c3 = new CompareExpression(emp.createSelectField("HIREDATE"), CompareExpression.IS_NOT_NULL, null);
        emp_list_query.getWhereClause().addCondition(c2);
        emp_list_query.getWhereClause().addCondition(Condition.OR, c3);

        OrderByField f1 = new OrderByField( emp_list_query.getField("ENAME"), OrderByField.DESCENT);
        OrderByField f2 = new OrderByField( new RawSqlExpression("trunc(HIREDATE)"), OrderByField.ASCENT);
        emp_list_query.addOrderByField(f1);
        emp_list_query.addOrderByField(f2);
                
        registry = new SqlBuilderRegistry();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testCreateSql(){
        String sql = registry.getSql(emp_list_query);
        assertNotNull(sql);
        System.out.println(sql);
        assertTrue(sql.indexOf("t1.ENAME AS EMPLOYEE_NAME")>0);
        assertTrue(sql.indexOf("t2.DNAME")>0);
        assertTrue(sql.indexOf("t2.LOC")>0);        
        assertTrue(sql.indexOf("sysdate")>0);        
        assertTrue(sql.indexOf("(t1.SAL > 2000 OR t1.HIREDATE IS NOT NULL)  AND t1.DEPTNO = t2.DEPTNO")>0);
        assertTrue(sql.indexOf("WHERE")>0);
        assertTrue(sql.indexOf("(t1.SAL > 2000 OR t1.HIREDATE IS NOT NULL)")>0);
        assertTrue(sql.indexOf("AND t1.DEPTNO = t2.DEPTNO")>0);
        assertTrue(sql.indexOf("FROM DEPT")>0);
        assertTrue(sql.indexOf("ORDER BY t1.ENAME DESC,trunc(HIREDATE) ASC")>0);
    }

}
