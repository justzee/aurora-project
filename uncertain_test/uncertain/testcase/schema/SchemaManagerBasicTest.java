/*
 * Created on 2009-7-18
 */
package uncertain.testcase.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Array;
import uncertain.schema.Attribute;
import uncertain.schema.Category;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;
import uncertain.schema.Schema;
import uncertain.schema.SchemaManager;

public class SchemaManagerBasicTest extends TestCase {

    static final String[] CORRECT_ORDER = {"TestDuplicate",  "BaseControl", "BaseButton", "Square", "NamedObject" };
    SchemaManager       schemaManager;
    
    public SchemaManagerBasicTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        schemaManager = new SchemaManager();
    }

    public void testSchemaOCM()
        throws Exception
    {
        Schema schema = schemaManager.loadSchemaFromClassPath("uncertain.testcase.schema.Sample");
        assertNotNull(schema);
        assertEquals(schema.getAttributes().length,2);
        assertEquals(schema.getTypes().length, 3);
        assertEquals(schema.getNameSpaces().length, 2);
        assertEquals(schema.getElements().length, 2);
        Element elm = schema.getElement( new QualifiedName("http://www.uncertain-framework.org/schema/simple-schema", "element"));
        assertNotNull(elm);

        assertEquals(elm.getArrays().length, 1);
        Array array = elm.getArray( new QualifiedName(null,"attributes"));
        assertNotNull(array);
        assertNotNull(array.getType());
        //IType type = array.getElementType();
        //assertNotNull(type);

        assertEquals(elm.getExtensions().length, 1);
        assertEquals(elm.getExtensions()[0].getBase(), "sc:complexType");
        QualifiedName qn = schema.getQualifiedName("ar:element");
        assertNotNull(qn);
        assertEquals("http://www.aurora-application.com/schema/", qn.getNameSpace());
    }
    
    public void testExtension()
        throws Exception
    {
        Schema schema = schemaManager.loadSchemaFromClassPath("uncertain.testcase.schema.extension_test");
        assertNotNull(schema);
        schema.doAssemble();

        Element btn = schema.getElement( new QualifiedName("http://myobjects.com/schema","Button"));
        assertNotNull(btn);
        Set set = new HashSet();
        set.addAll(btn.getAllExtendedTypes());
        List elements = btn.getAllElements();
        assertEquals(elements.size(), 2);
        
        IType type = schemaManager.getType( new QualifiedName("http://yet-another.schema.com/schema/", "NamedObject") );
        assertNotNull(type);
        assertTrue( type instanceof ComplexType);
        assertTrue(set.contains(type));
        ComplexType t = (ComplexType)type;
        assertEquals(t.getAttributes().length, 1);
        Attribute attrib = t.getAttributes()[0];
        assertEquals("name",attrib.getQName().toString());
        
        // test extended types
        List lst = btn.getAllExtendedTypes();
        assertEquals(lst.size(), CORRECT_ORDER.length);
        for(int i=0; i<lst.size(); i++){
            QualifiedName qname = ((ComplexType)lst.get(i)).getQName();
            assertEquals(qname.getLocalName(), CORRECT_ORDER[i]);
        }
        
        // test attached classes
        List cls_list = btn.getAllAttachedClasses();
        assertEquals(cls_list.size(), 5);
        
        assertEquals(schemaManager.getAllTypes().size(), 6);
    }
    
    public void testComponents()
        throws Exception
    {
        Schema schema = schemaManager.loadSchemaFromClassPath("aurora.testcase.ui.config.components");
        assertNotNull(schema);
        Element element = schemaManager.getElement( new QualifiedName(null,"select") );
        assertNotNull(element);
        List cls_list = element.getAllAttachedClasses();
        assertEquals(cls_list.size(), 5);
        Array array = element.getArray(new QualifiedName(null,"options") );
        assertNotNull(array);
        assertEquals(array.getElementType().getQName().getLocalName(), "option");
        
        CompositeMap select = new CompositeMap("select");
        CompositeMap options = select.createChild("options");
        CompositeMap option = options.createChild("option");
        option.put("value", "0");
        option.put("prompt", "test");
        
        Element elm = schemaManager.getElement(options);
        assertNotNull(elm);
        
    }
    
    public void testElementContain()
        throws Exception
    {
        Schema schema = schemaManager.loadSchemaFromClassPath("uncertain.testcase.schema.element_test");
        assertNotNull(schema);
        CompositeMap a= new CompositeMap("A");        
        CompositeMap c = a.createChild("B").createChild("C");
        Element element = schemaManager.getElement(c);
        assertNotNull(element);
        assertEquals(element.getAttributes().length, 2);
    }

    public void testRef()
        throws Exception
    {
        /*
        OCManager ocm = OCManager.getInstance();
        System.out.println(ocm.getReflectionMapper().getMappingRule(Category.class));
        */
        Schema schema = schemaManager.loadSchemaFromClassPath("uncertain.testcase.schema.ref_test");
        assertNotNull(schema);
        
        // test category
        Category c1 = schemaManager.getCategory(new QualifiedName("BasicControl"));
        assertNotNull(c1);        
        Category c2 = c1.getParentCategory();
        assertNotNull(c2);
        // test Attribute ref
        Element child = schema.getElement( new QualifiedName("child"));
        assertNotNull(child);
        List attribs = child.getAllAttributes();
        child.getAllAttributes();
        assertEquals(attribs.size(), 3);
        List child_elements = child.getAllElements();
        assertEquals(child_elements.size(), 2);
        // test getting ref attribute
        Element parent = schema.getElement( new QualifiedName("parent"));
        assertNotNull(parent);
        Attribute width = parent.getAttribute( new QualifiedName("Width"));
        assertNotNull(width);
        assertTrue(width.isRef());
        Attribute ref_width = width.getRefAttribute();
        assertNotNull(ref_width);
        assertTrue(ref_width.getQName().equals(width.getQName()));
        Category category = ref_width.getCategoryInstance();
        assertNotNull(category);
        
        // test element ref
        Element to_ref = child.getElement( new QualifiedName("ToRef"));
        assertNotNull(to_ref);
        assertTrue(to_ref.isRef());
        Element real_to_ref = (Element)to_ref.getRefType(); 
        assertNotNull(real_to_ref);
        assertEquals(real_to_ref.getQName(), to_ref.getQName());
        Category cr = real_to_ref.getCategoryInstance();
        assertNotNull(cr);
        assertEquals(cr.getQName().getLocalName(), "BasicControl");
        
    }

}
