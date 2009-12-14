/*
 * Created on 2009-12-11 下午12:37:08
 * Author: Zhou Fan
 */
package uncertain.testcase.composite;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class CompositeAccessTest extends TestCase {
    
    String[] PATHS = {}; 
    CompositeLoader     loader;
    CompositeMap        data;
    
    public CompositeAccessTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        loader = CompositeLoader.createInstanceWithExt("xml");        
        String path = CompositeAccessTest.class.getName();
        data = loader.loadFromClassPath(path);
        assertNotNull(data);
    }
    
    public void testGet()
        throws Exception
    {
        CompositeMap child1 = (CompositeMap)data.getObject("grand-father/parent/child");
        CompositeMap child2 = (CompositeMap)data.getObject("/grand-father/parent/child");
        assertNotNull(child1);
        assertTrue(child1==child2);
        
        CompositeMap parent = child1.getParent();
        String name = (String)parent.getObject("child/@name");
        assertEquals(name, "A3");
        String parent_name = (String)child1.getObject("/grand-father/parent/@name");
        assertEquals(parent_name, "A2");
        
        CompositeMap child3 = (CompositeMap)parent.getObject("child");
        assertTrue(child1==child3);
        
        CompositeMap grand1 = (CompositeMap)child1.getObject("../..");
        CompositeMap grand2 = data.getChild("grand-father");
        assertTrue(grand1==grand2);
        
        String grand_name = (String)child1.getObject("../../@name");
        assertEquals(grand_name,"A1");
    }
    
    public void testPut(){
        CompositeMap child1 = (CompositeMap)data.getObject("grand-father/parent/child");
        child1.putObject("../child2/@name", "new_child", true);
        String new_name = (String)data.getObject("grand-father/parent/child2/@name");
        assertEquals(new_name, "new_child");
    }

}
