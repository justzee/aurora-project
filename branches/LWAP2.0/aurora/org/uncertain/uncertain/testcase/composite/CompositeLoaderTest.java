/*
 * Created on 2009-8-21
 */
package uncertain.testcase.composite;

import java.util.Map;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class CompositeLoaderTest extends TestCase {

    public CompositeLoaderTest(String name) {
        super(name);
    }
    
    public void testNamespaceMapping()
        throws Exception
    {
        CompositeLoader loader = CompositeLoader.createInstanceWithExt("data");
        loader.setSaveNamespaceMapping(true);
        //loader.setCreateLocator(true);
        String pkg = this.getClass().getPackage().getName();
        String path = pkg + ".TestMap";
        CompositeMap data = loader.loadFromClassPath(path);
        assertNotNull(data);
        Map mapping = data.getNamespaceMapping();
        assertNotNull(mapping);
        assertEquals(mapping.size(), 2);
    }

}
