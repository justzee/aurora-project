/*
 * Created on 2005-10-9
 */
package uncertain.testcase.proc;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

/**
 * SwitchTest
 * @author Zhou Fan
 * 
 */
public class AssertTest extends ProcedureRunnerTest {
    
    public static final String[] KEYS =        {"null", "notnull", "eq",    "neq",   "gt",    "lt",        "goe","loe"};
    public static final Object[] RIGHT_VALUES = {null,    "hello", "hello", "world", "1.345", new Long(1), "0", new Double(-1)};
    
    
    public void doNormalAssertTest() throws Exception {
        loadProcedure("AssertTest1.xml");
        assertNotNull(test_proc);
        assertNotNull(test_proc.getEntryList());
        ProcedureRunner runner = new ProcedureRunner(test_proc);
        //runner.setHandleManager(handle_manager);
        runner.setConfiguration(super.config);
        CompositeMap context = runner.getContext();
        for(int i=0; i<KEYS.length; i++){
            String key = "/test/@"+KEYS[i];
            context.putObject(key,RIGHT_VALUES[i],true);            
        }
        context.putObject("/test/@number", "100", true);
        runner.run();
        Throwable thr = runner.getException();
        if(thr!=null)thr.printStackTrace();
        assertNull(thr);        
    }
    
    public void doMessageTest() throws Exception {
        // load test script
        loadProcedure("AssertTest2.xml");
        assertNotNull(test_proc);
        assertNotNull(test_proc.getEntryList());
        ProcedureRunner runner = new ProcedureRunner(test_proc);
        //runner.setHandleManager(handle_manager);
        runner.setConfiguration(super.config);
        // prepare input data
        CompositeMap context = runner.getContext();
        context.put("gt", "1");
        context.put("eq", "hello");
        // 1. test for a default assertion fail
        context.put("test", "normal");
        AssertionError ae = null;
        runner.run();            
        ae = (AssertionError)runner.getException();
        assertNotNull(ae);

        // 2. test for assertion fail with custormized message
        runner.reset();
        //context = runner.getContext();
        context.put("test","msg");
        ae = null;
        runner.run();
        ae = (AssertionError)runner.getException();
        assertNotNull(ae);
        assertEquals(ae.getMessage(), "hello world"); 
    }
    
    public void testRun() throws Exception {
        //handle_manager = new HandleManager();        
        doNormalAssertTest();
        doMessageTest();       
    }

    /**
     * Constructor for SwitchTest.
     * @param arg0
     */
    public AssertTest(String name) {
        super(name);
    }

    
}
