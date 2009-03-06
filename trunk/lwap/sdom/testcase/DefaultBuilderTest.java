/*
 * DefaultBuilderTest.java
 *
 * Created on 2001年12月25日, 下午11:47
 */

package sdom.testcase;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import sdom.DOMNode;
import sdom.DefaultDOMNodeBuilder;
import sdom.DocumentParser;

/**
 *
 * @author  Zhou Fan
 * @version 
 */
public class DefaultBuilderTest extends TestCase {
    
    DOMNode root;
    DefaultDOMNodeBuilder builder;
    DocumentParser parser;
    Class[] expected_class = {DOMNode.class, TestClass.class, MyDOMNode.class};
        
    /** setup test case */
    protected void setUp(){
        try{
        InputStream stream = DefaultBuilderTest.class.getClassLoader().getResourceAsStream("sdom/testcase/testcase.xml");
        builder = new DefaultDOMNodeBuilder("sdom.testcase", "set_");
        builder.setPackageMapping( "my.dom.com", "sdom.testcase");
        builder.setPackageMapping("www.sdom.org", "sdom.testcase");
        parser = new DocumentParser( builder );
        root = parser.parse( stream);
        //System.out.println( root.toString());
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void testExactClass(){
        List childs = root.getChilds();
        assertTrue( childs.size() == 3);
        Iterator it = root.getChildIterator();
        int i=0;
        while(it.hasNext()){
            Object node = it.next();
            assertEquals(node.getClass(), expected_class[i++]);
        }
    }
    
    /** Creates new DefaultBuilderTest */
    public DefaultBuilderTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite( DefaultBuilderTest.class );
     }
}
