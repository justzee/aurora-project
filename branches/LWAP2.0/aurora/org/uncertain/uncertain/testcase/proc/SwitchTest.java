/*
 * Created on 2005-10-9
 */
package uncertain.testcase.proc;

import uncertain.composite.CompositeMap;
import uncertain.proc.ProcedureRunner;

//import java.io.*;

/**
 * SwitchTest
 * @author Zhou Fan
 * 
 */
public class SwitchTest extends ProcedureRunnerTest {
    
    public static final String KEY_RESULT = "ResultValue";
    public static final String KEY_INIT = "InitValue";
    
    public void doSwitchTest(String initialValue, String resultValue, String proc_name) 
    throws Exception {
        
        //handle_manager = new HandleManager();
        //handle_manager.addParticipant(this);   
        //oc_manager.getLogger().info(handle_manager.getHandleMap().toString());
        
        loadProcedure(proc_name);
        ProcedureRunner runner = new ProcedureRunner(test_proc);
        CompositeMap context = runner.getContext();
        context.put(KEY_INIT, initialValue);
        config.addParticipant(this);
        runner.setConfiguration(config);
        //runner.setHandleManager(handle_manager);
        
        runner.run();
        
        //oc_manager.getLogger().info(context.toXML());
        Object obj = context.get(KEY_RESULT);
        if(resultValue == null) assertNull(obj);
        else assertEquals(resultValue,obj);
    }
    
    public void testSwitchTag() throws Exception {
        doSwitchTest("Normal match", "Normal match", "SwitchTest.xml");
        doSwitchTest(null,null, "SwitchTest.xml");
        doSwitchTest("Any words", "NotNullMatch", "SwitchTest.xml");
        doSwitchTest(null, "DefaultMatch", "SwitchTest2.xml");
    }
    

    
    public void testRun() throws Exception {
        
    }

    /**
     * Constructor for SwitchTest.
     * @param arg0
     */
    public SwitchTest(String arg0) {
        super(arg0);
    }
    
    public void onNormalMatch(ProcedureRunner runner){
        oc_manager.getLogger().log("onNormalMatch called ");
        runner.getContext().put(KEY_RESULT, "Normal match");
    }
    
    public void onNullMatch(ProcedureRunner runner){
        runner.getContext().put(KEY_RESULT,null);
    }
    
    public void onNotNullMatch(ProcedureRunner runner){
        runner.getContext().put(KEY_RESULT,"NotNullMatch");
    }
    
    public void onDefaultMatch(ProcedureRunner runner){
        runner.getContext().put(KEY_RESULT,"DefaultMatch");
    }
    
    public void onWrongMatchMatch(ProcedureRunner runner){
        runner.getContext().put(KEY_RESULT,"WrongMatch");
    }
    
}
