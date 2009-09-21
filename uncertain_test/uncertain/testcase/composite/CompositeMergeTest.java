/*
 * Created on 2008-6-3
 */
package uncertain.testcase.composite;

import java.util.List;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.DynamicObject;
import uncertain.composite.decorate.AttributeModify;
import uncertain.composite.decorate.ElementModifier;

public class CompositeMergeTest extends TestCase {
    
    CompositeLoader loader = new CompositeLoader();
    
    public CompositeMap getSource()
        throws Exception
    {
        return loader.loadFromClassPath("uncertain.testcase.composite.merge_source");
    }

    public CompositeMergeTest(String arg0) {
        super(arg0);
    }
    
    
    
    public void testInsert()
        throws Exception
    {
        ElementModifier t = ElementModifier.createElementInsert();
        t.setPath("/fields");
        
        CompositeMap f1 = new CompositeMap("field");
        f1.put("name", "NEW_FIELD1");
        CompositeMap f2 = new CompositeMap("field");
        f2.put("name", "NEW_FIELD2");
        t.getObjectContext().addChild(f1);
        t.getObjectContext().addChild(f2);
        
        doInsertTest(t);
        
        CompositeMap map = loader.loadFromClassPath("uncertain.testcase.composite.InsertTest");
        ElementModifier m = (ElementModifier)DynamicObject.cast(map, ElementModifier.class);
    }
    
    public void doInsertTest( ElementModifier t )
        throws Exception
    {
        CompositeMap source = getSource();
        assertNotNull(source);
        
        t.process(source);
        List childs = source.getChild("fields").getChilds();
        assertNotNull(childs);
        assertEquals(  ((CompositeMap)childs.get(0)).get("name"), "NEW_FIELD1");
        assertEquals(  ((CompositeMap)childs.get(1)).get("name"), "NEW_FIELD2");

        int size = source.getChilds().size();
        
        ElementModifier t1 = ElementModifier.createElementInsert();
        t1.process(source);
        assertEquals(size, source.getChilds().size());               
    }

    public void testAppend()
        throws Exception
    {
        ElementModifier t = ElementModifier.createElementAppend();
        t.setPath("/fields");
        
        CompositeMap f1 = new CompositeMap("field");
        f1.put("name", "NEW_FIELD1");
        CompositeMap f2 = new CompositeMap("field");
        f2.put("name", "NEW_FIELD2");
        t.getObjectContext().addChild(f1);
        t.getObjectContext().addChild(f2);
        
        CompositeMap source = getSource();
        assertNotNull(source);
        
        t.process(source);
        List childs = source.getChild("fields").getChilds();
        assertNotNull(childs);
        assertEquals(childs.get(2), f1);
        assertEquals(childs.get(3), f2);
        int size = source.getChilds().size();
        
        ElementModifier t1 = ElementModifier.createElementAppend();
        t1.process(source);
        assertEquals(size, source.getChilds().size());
    }
    
    public void testRemove()
        throws Exception
    {
        CompositeMap source = getSource();
        assertNotNull(source);        
        ElementModifier t = ElementModifier.createElementRemove();
        t.setPath("/fields");
        t.process(source);
        assertNull(source.getChild("fields"));
        
        source = getSource();
        t = ElementModifier.createElementRemove();
        t.setRootPath("/fields");
        t.setKeyField("name");
        t.setKeyValue("NAME");
        t.process(source);
        CompositeMap m = source.getChild("fields");
        assertNull(CompositeUtil.findChild(m, "field", "name", "NAME"));
    }
    
    public void testMerge()
        throws Exception
    {
        CompositeMap source = getSource();
        assertNotNull(source);        
        ElementModifier t = ElementModifier.createElementMerge();
        t.setRootPath("/fields");
        t.setKeyField("name");
        t.setKeyValue("NAME");        
        CompositeMap m = new CompositeMap("field");
        m.put("dataSource", "/parameter/@field");
        m.createChild("validator").put("class", "default-validator");
        t.getObjectContext().addChild(m);
        t.process(source);
        
        CompositeMap merged = CompositeUtil.findChild(source, "field", "name", "NAME");
        assertNotNull(merged);
        assertEquals(merged.getString("dataSource"), "/parameter/@field");
        assertNotNull(merged.getChild("validator"));

    }
    
    public void testReplace()
        throws Exception
    {
        CompositeMap source = getSource();
        assertNotNull(source);        
        ElementModifier t = ElementModifier.createElementReplace();
        t.setRootPath("/fields");
        t.setKeyField("name");
        t.setKeyValue("NAME");        
        CompositeMap m = new CompositeMap("field");
        m.put("dataSource", "/parameter/@field");
        m.createChild("validator").put("class", "default-validator");
        t.getObjectContext().addChild(m);
        t.process(source);
        
        CompositeMap merged = CompositeUtil.findChild(source, "field", "dataSource", "/parameter/@field");
        assertNotNull(merged);
        assertNull(merged.getString("name"));

    }
    
    public void testAttributeModify()
        throws Exception    
    {
        CompositeMap source = getSource();
        assertNotNull(source);        
        ElementModifier t = ElementModifier.createAttributeModify();
        t.setRootPath("/fields");
        t.setKeyField("name");
        t.setKeyValue("NAME");        
        AttributeModify am1 = AttributeModify.createAttributeSet("name", "NAME1");
        AttributeModify am2 = AttributeModify.createAttributeSet("dataType", "String");   
        AttributeModify am3 = AttributeModify.createAttributeRemove("required");
        t.addAttributeModify(am1);
        t.addAttributeModify(am2);
        t.addAttributeModify(am3);
        t.process(source);        
        CompositeMap m = CompositeUtil.findChild(source, "field", "name", "NAME1");
        assertNotNull(m);
        assertNull(m.get("required"));
        assertEquals(m.get("dataType"), "String");
    }

}
