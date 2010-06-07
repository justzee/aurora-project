/*
 * Created on 2009-11-10 下午01:28:18
 * Author: Zhou Fan
 */
package uncertain.testcase.composite;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.XMLOutputter;

public class XMLOutputterTest extends TestCase {

    CompositeLoader loader;

    public XMLOutputterTest(String name) {
        super(name);
        loader = new CompositeLoader();
    }

    public void testGeneral() throws Exception {
        String xml = null, xml2 = null;
        {
            CompositeMap root = new CompositeMap("r", "http://somesite.com",
                    "root");
            root.put("attrib1", "value1");
            root.put("attrib2", "value2");
            root.createChild("no_ns_child1");
            root.createChild("no_ns_child2");
            root.createChild("child_with_cdata").setText(
                    "<Sample CDATA></Sample CDATA>");

            CompositeMap parent = root;
            for (int i = 1; i < 10; i++) {
                CompositeMap child = new CompositeMap("ns" + i,
                        "http://yetanothersite" + i + ".com", "child" + i);
                parent.addChild(child);
                parent = child;
            }
            parent.createChild("ns8", "http://yetanothersite8.com",
                    "anotherchild").put("Key", "Value");
            parent.getParent().createChild("ns9", "http://yetanothersite9.com",
                    "another_child9");
            root.createChild("ns1", "http://differentsite.com",
                    "child_with_diff_ns1").put("K1", "V1");

            xml = root.toXML();
            //System.out.println(xml);
            xml2 = XMLOutputter.defaultInstance().toXML(root, true);
            //System.out.println(xml2);
        }
        assertTrue(xml.indexOf("r:root xmlns:r=\"http://somesite.com\"") > 0);
        for (int i = 1; i < 10; i++) {
            String to_find = "ns" + i + ":child" + i + " xmlns:ns" + i
                    + "=\"http://yetanothersite" + i + ".com\"";
            assertTrue(xml.indexOf(to_find) > 0);
        }
        assertTrue(xml.indexOf("ns8:anotherchild Key=\"Value\"") > 0);
        assertTrue(xml.indexOf("<no_ns_child1/>") > 0);

        // System.out.println(root.toXML());

        CompositeMap parsed_back = loader.loadFromString(xml);
        CompositeMap child9 = CompositeUtil.findChild(parsed_back, "child9");
        assertNotNull(child9);
        assertEquals(child9.getNamespaceURI(), "http://yetanothersite9.com");

        CompositeMap child_with_cdata = CompositeUtil.findChild(parsed_back,
                "child_with_cdata");
        assertNotNull(child_with_cdata);
        assertEquals(child_with_cdata.getText(),
                "<Sample CDATA></Sample CDATA>");

        CompositeMap parsed_back2 = loader.loadFromString(xml2);
        assertNotNull(parsed_back2);
        CompositeMap child_with_diff_ns1 = CompositeUtil.findChild(
                parsed_back2, "child_with_diff_ns1");
        assertNotNull(child_with_diff_ns1);
        assertEquals(child_with_diff_ns1.getNamespaceURI(), "http://differentsite.com");
        assertEquals(child_with_diff_ns1.get("K1"), "V1");
    }

}
