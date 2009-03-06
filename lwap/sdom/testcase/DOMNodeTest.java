/*
 * DOMNodeTest.java
 *
 * Created on 2001年9月19日, 上午3:02
 */

package sdom.testcase;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sdom.DOMNode;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DOMNodeTest extends TestCase {
    
    DOMNode node1,node2, node3, node4;

    /** setup test case */
    protected void setUp(){
           node1 = new DOMNode("parent");
           node2 = new DOMNode("child");  
           assertTrue( node1.getAttributes() == null);
           assertTrue( node1.getChilds() == null);
           node1.setAttribute("name", "parent");
           node2.setAttribute("name", "child");
           node1.addChild(node2);
           
           node3 = new DOMNode( null, "name");
           node4 = new DOMNode( "URI", "name");
    }
    
    public void testNames(){
        //   assertTrue( !node3.equals(node4));
           assertEquals( node3.getQName(), "name");
         //  assertEquals( node4.getQName(), "URI:name");
    }
    
    public void testEquals(){
          assertEquals( node1, new DOMNode("parent"));
          assertEquals( node1, "parent");
    }
    
    public void testAttributes(){
           assertEquals( node1.getAttribute("name"), "parent");
           assertEquals( node1.getChild("child").getAttribute("name"), "child");
    }
    
    public void testChilds(){
           assertEquals( node1.getChilds().size(), 1);
           assertEquals( node1.getChild("child"), node2);
           assertEquals( node1.getChild(node2), node2);
    }
    
    /** Creates new DOMNodeTest */
    public DOMNodeTest (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite( DOMNodeTest.class );
     }
}
