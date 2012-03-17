/*
 * Created on 2005-10-31
 */
package uncertain.testcase.composite;

import junit.framework.TestCase;
import uncertain.composite.*;


/**
 * CompositeMapTest
 * @author Zhou Fan
 * 
 */
public class CompositeMapTest extends TestCase {
    
    CompositeLoader	loader = null;

    /**
     * Constructor for CompositeMapTest.
     * @param arg0
     */
    public CompositeMapTest(String arg0) {
        super(arg0);
        loader = new CompositeLoader();
    }

    public void testGetText() throws Exception {
        CompositeMap m = loader.loadFromClassPath("uncertain.testcase.composite.TextTest");
        assertNotNull(m);
        assertEquals("success",m.getText());
        //System.out.println(m.toXML());
    }

}
