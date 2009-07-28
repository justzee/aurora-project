/*
 * Created on 2009-7-18
 */
package uncertain.testcase.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.CollectionRef;
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
        Schema schema = schemaManager.loadSchemaByClassPath("uncertain.testcase.schema.Sample");
        assertNotNull(schema);
        assertEquals(schema.getAttributes().length,2);
        assertEquals(schema.getTypes().length, 3);
        assertEquals(schema.getNameSpaces().length, 2);
        assertEquals(schema.getElements().length, 1);
        Element elm = schema.getElements()[0];
        assertEquals(elm.getCollections().length, 2);
        CollectionRef col = elm.getCollections()[0];
        assertEquals(col.getMinOccur(), "0");
        assertEquals(col.getName(), "attributes");
        assertEquals(elm.getExtensions().length, 1);
        assertEquals(elm.getExtensions()[0].getBase(), "sc:complexType");
        QualifiedName qn = schema.getQualifiedName("ar:element");
        assertNotNull(qn);
        assertEquals("http://www.aurora-application.com/schema/", qn.getNameSpace());
    }
    
    public void testExtension()
        throws Exception
    {
        Schema schema = schemaManager.loadSchemaByClassPath("uncertain.testcase.schema.extension_test");
        assertNotNull(schema);
        schema.doAssemble();
        Element[] elements = schema.getElements();
        Element e = null;
        for(int i=0; i<elements.length; i++){
            if( "Button".equals(elements[i].getLocalName())){
                e = elements[i];
                break;
            }
        }
        assertNotNull(e);
        Set set = new HashSet();
        set.addAll(e.getAllExtendedTypes());
        
        IType type = schemaManager.getType( new QualifiedName("http://yet-another.schema.com/schema/", "NamedObject") );

        assertNotNull(type);
        assertTrue( type instanceof ComplexType);
        assertTrue(set.contains(type));
        ComplexType t = (ComplexType)type;
        assertEquals(t.getAttributes().length, 1);
        Attribute attrib = t.getAttributes()[0];
        assertEquals("name",attrib.getQName().toString());
        
        // test extended types
        List lst = e.getAllExtendedTypes();
        assertEquals(lst.size(), CORRECT_ORDER.length);
        for(int i=0; i<lst.size(); i++){
            QualifiedName qname = ((ComplexType)lst.get(i)).getQName();
            assertEquals(qname.getLocalName(), CORRECT_ORDER[i]);
        }
        
        // test attached classes
        List cls_list = e.getAllAttachedClasses();
        assertEquals(cls_list.size(), 5);
        
        assertEquals(schemaManager.getAllTypes().size(), 6);
    }

}
