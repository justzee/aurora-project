/*
 * Created on 2009-8-21
 */
package uncertain.testcase.composite;

import java.util.Map;

import junit.framework.TestCase;
import uncertain.cache.MapBasedCache;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

public class CompositeLoaderTest extends TestCase {
    
    static final int LOOP_SIZE = 5000;

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
    
    public void testLoadDataWithCache()
        throws Exception
    {
        MapBasedCache   cache = new MapBasedCache();
        CompositeLoader loader = CompositeLoader.createInstanceWithExt("xml");
        loader.setCacheEnabled(true);
        loader.setCache(cache);
        
        String name = CompositeLoaderTest.class.getPackage().getName()+".ComplexFile";
        CompositeMap data = loader.loadFromClassPath(name);
        assertNotNull(data);
        assertEquals(cache.getRequestCount(),1);
        assertEquals(cache.getHitCount(),0);
        
        long tick = System.currentTimeMillis();
        for(int i=0; i<LOOP_SIZE-1; i++)
            data = loader.loadFromClassPath(name);
        tick = System.currentTimeMillis()-tick;
        assertEquals(cache.getRequestCount(),LOOP_SIZE);
        assertEquals(cache.getHitCount(),LOOP_SIZE-1);
        System.out.println("time with cache:"+tick);
        
        // after disable cache, request count should stay unchanged
        loader.setCacheEnabled(false);
        tick = System.currentTimeMillis();
        for(int i=0; i<LOOP_SIZE-1; i++)
            data = loader.loadFromClassPath(name);
        tick = System.currentTimeMillis()-tick;
        assertEquals(cache.getRequestCount(),LOOP_SIZE);
        System.out.println("time without cache:"+tick);
        
    }

}
