/*
 * Created on 2005-7-25
 */
package uncertain.testcase.core;

import junit.framework.TestCase;
import uncertain.core.*;
import uncertain.composite.*;
import uncertain.proc.*;
import uncertain.ocm.*;
import uncertain.testcase.proc.ParticipantTest2;
import java.io.*;

/**
 * UncertainEngineTest
 * @author Zhou Fan
 * 
 */
public class UncertainEngineTest extends TestCase {

    protected UncertainEngine		engine;
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }
    static String baseDir;
    
    public static UncertainEngine createEngine() throws Exception {
        CompositeMap          config  = null;
        String                dir = null;

        UncertainEngine engine = null;
        ClassLoader cls_loader = UncertainEngineTest.class.getClassLoader(); 
        InputStream is = cls_loader.getResourceAsStream("uncertain/testcase/core/engine_config.xml");        
        assertNotNull(is);
        
        // Iterate throughout config and replace 'basedir' property with real path
        dir = cls_loader.getResource("uncertain/testcase/core/").getFile();        
        config = OCManager.getDefaultCompositeLoader().loadFromStream(is);
        assertNotNull(config);
        IterationHandle handle = new IterationHandle(){
            
            String baseDir;
            
            public IterationHandle Init(String bd){
                baseDir = bd;
                return this;
            }

            public int process( CompositeMap map){
                Object dir = map.get("basedir");
                if(dir!=null) {
                    String realDir = dir.toString().replaceAll("basepath",baseDir);
                    //System.out.println(realDir);                            
                    map.put("basedir",realDir);
                }
                return IterationHandle.IT_CONTINUE;
            }
        }.Init(dir);

        config.iterate( handle, true);
        engine = new UncertainEngine(config);
        engine.scanConfigFiles();
        return engine;
    }

    
    /**
     * Constructor for UncertainEngineTest.
     * @param arg0
     */
    public UncertainEngineTest(String arg0) throws Exception {
        super(arg0);
        engine = createEngine();
    }
    
    /*
    public void testInitializeResult(){
        Object o = engine.getObjectSpace().getInstanceOfType(GlobalResource.class);
        assertNotNull(o);
    }
    */
    
    /*
    public void testGetDocument(){
        CompositeLoader l = engine.getCompositeLoader();
        assertNotNull(l.getFile("engine_config.xml"));
        assertNotNull(l.getFile("ProcTest.xml"));
    }
    */

    public void testLoadProcedure(){
        Procedure p = engine.loadProcedure("uncertain.testcase.proc.ProcTest");
        assertNotNull(p);
    }
    
    public void testRunProcedure(){
        ProcedureRunner runner = engine.createProcedureRunner(
                "uncertain.testcase.proc.ProcTest",
                "uncertain.testcase.proc.ProcConfig");
        assertNotNull(runner);
        //assertNotNull(runner.getConfig_map().getChilds());
        
        runner.run();
        assertNull(runner.getException());
        CompositeMap m = runner.getContext();
        ParticipantTest2 test2 = (ParticipantTest2)m.get(ParticipantTest2.RESULT);
        assertNotNull(test2);
        assertTrue(!test2.getFlag());

    }
}
