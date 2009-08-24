/*
 * Created on 2009-8-21
 */
package uncertain.testcase.composite;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class CompositeMapParserTest extends TestCase {

    public CompositeMapParserTest(String name) {
        super(name);
    }
    
    public void testLocator()
        throws Exception
    {
        CompositeLoader loader = CompositeLoader.createInstanceWithExt("data");
        //loader.setCreateLocator(true);
        String pkg = this.getClass().getPackage().getName();
        String path = pkg + ".TestMap";
        CompositeMap data = loader.loadFromClassPath(path);
        assertNotNull(data);
    }

}
