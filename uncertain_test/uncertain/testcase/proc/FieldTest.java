/*
 * Created on 2010-6-3 下午05:29:02
 * $Id$
 */
package uncertain.testcase.proc;

import uncertain.proc.Action;
import uncertain.proc.Case;
import uncertain.proc.Field;
import uncertain.proc.Switch;

public class FieldTest extends ProcedureRunnerTest {

    public FieldTest(String name) {
        super(name);
    }

    public void testGetField()
        throws Exception
    {
        loadProcedure("FieldTest.xml");
        assertNotNull(super.test_proc);
        
        Field f1 = test_proc.getField("BusinessModel");
        assertNotNull(f1);
        
        Switch sw = (Switch)test_proc.getNamedEntry("switch");
        assertNotNull(sw);
        
        Case cs = sw.getCaseByName("AutoGenerate");
        assertNotNull(cs);
        Field f2 = cs.getField("BusinessModel");
        assertNotNull(f2);
        assertTrue(f1==f2);
        
        Action action = (Action)cs.getNamedEntry("Test1");
        assertNotNull(action);
        //action.
        
    }
    
    

}
