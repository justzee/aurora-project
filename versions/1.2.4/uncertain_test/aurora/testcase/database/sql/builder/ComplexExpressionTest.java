/*
 * Created on 2008-3-28
 */
package aurora.testcase.database.sql.builder;

import junit.framework.TestCase;
import aurora.database.sql.ComplexExpression;
import aurora.database.sql.SelectField;
import aurora.database.sql.SelectSource;

public class ComplexExpressionTest extends TestCase {
    
    String expression1 = "decode({1}, 'ADAMS', {1}, 'ALLEN', {2})";
    SelectSource  from1, from2;
    SelectField           f1, f2;

    public ComplexExpressionTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        from1 = new SelectSource("EMP");
        from2 = new SelectSource("DEPT");
        from1.setAlias("e");
        from2.setAlias("d");
        f1 = from1.createSelectField("ENAME");
        f2 = from2.createSelectField("DNAME");
        
    }
    
    public void testTranslatedExpression(){
        ComplexExpression exp = new ComplexExpression(expression1);
        exp.defineField(f1);
        exp.defineField(f2);
        String str = exp.getTranslatedExpression();
        assertNotNull(str);
        assertEquals(str,"decode(e.ENAME, 'ADAMS', e.ENAME, 'ALLEN', d.DNAME)");
        System.out.println();
    }

}
