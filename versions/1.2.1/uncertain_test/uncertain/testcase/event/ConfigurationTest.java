/*
 * Created on 2009-12-4 下午06:20:27
 * Author: Zhou Fan
 */
package uncertain.testcase.event;

import junit.framework.TestCase;
import uncertain.event.Configuration;
import uncertain.proc.ProcedureRunner;

public class ConfigurationTest extends TestCase {

    public ConfigurationTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testEventListener()
        throws Exception
    {
        String p1 = new String("p1");
        Integer p2 = new Integer(999);
        
        StringBuffer content = new StringBuffer();
        
        ProcedureRunner runner = new ProcedureRunner();
        EventRecorder parent_recorder = new EventRecorder("parent", content);
        Configuration parent = new Configuration();
        parent.addParticipant(parent_recorder);
        
        EventRecorder child_recorder = new EventRecorder("child", content);
        Configuration child = new Configuration();
        child.addParticipant(child_recorder);
        child.setParent(parent);
        
        child.fireEvent("BeginService", new Object[]{p1,p2} );
        
        System.out.println(content.toString());
    }

}
