/*
 * Created on 2010-8-18 下午02:37:09
 * $Id$
 */
package uncertain.testcase.composite;

import java.util.Map;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;

public class XMLOutputWithMultiNamespaceTest extends TestCase {
    
    CompositeLoader     loader;
    static final String              TEST_FILE = XMLOutputWithMultiNamespaceTest.class.getPackage().getName() + ".NamespaceText";

    public XMLOutputWithMultiNamespaceTest(String name) {
        super(name);
        loader = CompositeLoader.createInstanceWithExt("xml");
        loader.setSaveNamespaceMapping(true);
    }
    
    public void testLoadAndToXML()
        throws Exception
    {
        CompositeMap root = loader.loadFromClassPath(TEST_FILE);
        XMLOutputter writter = XMLOutputter.defaultInstance();
        String parsed = writter.toXML(root, true);
        
        CompositeMap new_root = loader.loadFromString(parsed);
        Map mapping = new_root.getNamespaceMapping();
        String[] must_exist_url = { "url1.com", "url2.com", "test", "url3.com", "url4.com"};
        for(int i=0; i<must_exist_url.length; i++)
            assertTrue(mapping.containsKey(must_exist_url[i]));
    }

}
