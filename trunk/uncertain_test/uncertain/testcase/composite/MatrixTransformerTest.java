/*
 * Created on 2005-11-18
 */
package uncertain.testcase.composite;

import junit.framework.TestCase;
import uncertain.composite.*;
import uncertain.composite.transform.*;

/**
 * MatrixTransformerTest
 * @author Zhou Fan
 * 
 */
public class MatrixTransformerTest extends TestCase {

    CompositeLoader loader = new CompositeLoader(".");
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();       
    }

    /**
     * Constructor for MatrixTransformerTest.
     * @param arg0
     */
    public MatrixTransformerTest(String arg0) {
        super(arg0);
    }

    public void testTransform() throws Exception {
        CompositeMap data = loader.loadFromClassPath("uncertain.testcase.composite.demodata");
        assertNotNull(data);
        MatrixTransformer t = new MatrixTransformer(
                "DISTRIBUTOR_ID",
                "ITEM_ID,ITEM_NAME",
                "SELL_PRICE",
                false);
        CompositeMap r1 = t.transform(data);
        assertEquals(r1.getChilds().size(),5);
        //System.out.println(r1.toXML());
    }

}
