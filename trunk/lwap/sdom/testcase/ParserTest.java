/*
 * ParserTest.java
 *
 * Created on 2001年9月19日, 上午4:20
 */

package sdom.testcase;
import java.io.Reader;
import java.io.StringReader;

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
public class ParserTest extends TestCase {
    
    DocumentParser parser; // = DocumentParser.newInstance("sdom.testcase");
    static String xml_text[] = {
        "<?xml version=\"1.0\" ?>\r\n<parentnode name=\"ab\" childs=\"3\"><node>A</node><MyDOMNode Name=\"MyDOM\"/><my:MyDOMNode xmlns:my=\"http://mydomnode\" Name=\"MyDOM2\"/><xsl:child xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" name=\"b\"><nobody/><child name=\"c\"/></xsl:child>\r\n</parentnode>"
    };

    Reader getReader( int id){
       return  new StringReader( xml_text[id] );
    }
    
    /** setup test case */
    protected void setUp(){
    	DefaultDOMNodeBuilder db = new DefaultDOMNodeBuilder("sdom.testcase");
    	db.setPackageMapping("http://mydomnode", "sdom.testcase");
    	parser = new DocumentParser(db);
    }
    
    public void testDefaultDOM() throws Exception {
        DOMNode node = parser.parse( getReader(0));
        System.out.println("Parsed node:");
        System.out.println( node);
/*
        List list = node.getChilds();
        Iterator it = list.iterator();
        while( it.hasNext()){ 
            System.out.println( it.next());
        }
 */
        
        assertEquals( node.getChild("node").getValue(),"A");
        assertEquals(node.getAttribute("name"),"ab");
        assertTrue( node.getChilds().size() >0);
        MyDOMNode mn= (MyDOMNode) node.getChild("MyDOMNode");
        assertEquals( mn.get_Name(), "MyDOM");
    }
    
    /** Creates new ParserTest */
    public ParserTest (String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite( ParserTest.class );
     }
     
     public static void main(String[] args) throws Exception {
         System.out.println(xml_text[0]);
           ParserTest test = new ParserTest("name");
           test.setUp();
           test.testDefaultDOM();
           System.out.println("OK");
     }
}
