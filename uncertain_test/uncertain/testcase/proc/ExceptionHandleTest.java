/*
 * Created on 2007-6-5
 */
package uncertain.testcase.proc;

import java.io.IOException;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.proc.ProcedureRunner;
import uncertain.testcase.core.UncertainEngineTest;
import uncertain.testcase.object.TestExceptionHandle;

public class ExceptionHandleTest extends TestCase {
    
    UncertainEngine engine;
    ProcedureRunner runner;
    Configuration config;

    public ExceptionHandleTest(String arg0) {
        super(arg0);
    }

    protected void load(String proc) throws Exception {
        super.setUp();
        engine = UncertainEngineTest.createEngine();
        runner = engine.createProcedureRunner(proc);
        config = engine.createConfig();
        config.addParticipant( new TestExceptionHandle());
        runner.addConfiguration(config);
        
    }
    
    protected void setUp() throws Exception {
        load("uncertain.testcase.proc.ExceptionTest");
    }
    
    void setException(String exp){
        CompositeMap context = runner.getContext();
        context.put(TestExceptionHandle.KEY_EXCEPTION, exp);
    }
    
    public void testCatchWithDestination(){
        setException("java.io.IOException");
        runner.run();
        assertNull(runner.getException());
        Boolean success = runner.getContext().getBoolean("success");
        assertNotNull(success);
        assertTrue(success.booleanValue());
        Object o = runner.getContext().get("result");
        assertTrue(o instanceof IOException);
    }
    
    public void testCatchWithActions(){
        
        setException("java.lang.IllegalStateException");
        runner.run();
        assertNull(runner.getException());
        
        CompositeMap c = runner.getContext();        
        Boolean success = c.getBoolean("success");
        assertNotNull(success);
        assertTrue(success.booleanValue());
        assertEquals("true", c.get("set1"));
        assertEquals("true", c.get("set2"));
        
    }
    
    public void testTrowAgain(){
        setException("java.sql.SQLException");
        runner.run();
        assertNotNull(runner.getException());
        Throwable ex = runner.getException();
        assertTrue(ex instanceof IllegalAccessException);
    }
    
    public void testAnyException() throws Exception {
        
        load("uncertain.testcase.proc.ExceptionTest2");  
        setException("java.io.IOException");
        runner.run();
        assertNull(runner.getException());
        Boolean success = runner.getContext().getBoolean("success");
        assertNotNull(success);
        assertTrue(success.booleanValue());
        Object o = runner.getContext().get("result");
        assertTrue(o instanceof IOException);
        assertNull(runner.getContext().get("unexpect"));
        
        load("uncertain.testcase.proc.ExceptionTest2");        
        setException("java.lang.IllegalAccessException");
        runner.run();
        assertTrue(runner.getContext().getBoolean("any", false));
        //assertNotNull(runner.getContext().get("unexpect"));
        
    }


}
