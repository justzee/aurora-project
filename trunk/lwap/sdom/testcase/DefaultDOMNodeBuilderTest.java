/*
 * DefaultDOMNodeBuilderTest.java
 *
 * Created on 2001年9月19日, 上午3:32
 */

package sdom.testcase;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sdom.DOMNode;
import sdom.DefaultDOMNodeBuilder;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DefaultDOMNodeBuilderTest extends TestCase {

    DefaultDOMNodeBuilder builder;
    
    /** setup test case */
    protected void setUp(){
       builder = new DefaultDOMNodeBuilder( "sdom.testcase", "set_");    
    }
    
    public void testGets(){
       DOMNode node = builder.getDOMNode(null, "MyDOMNode");
       assertEquals( node.getClass().getName(), "sdom.testcase.MyDOMNode");
       assertEquals( builder.getAttributeSetMethod("My","Node"), "set_MyNode");
       
    }
    
    /** Creates new DefaultDOMNodeBuilderTest */
    public DefaultDOMNodeBuilderTest (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite( DefaultDOMNodeBuilderTest.class );
     }
}
