/*
 * Created on 2009-7-18
 */
package uncertain.testcase.schema;

import uncertain.composite.CompositeMap;
import uncertain.demo.ocm.BaseOCMTestCase;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.ocm.PackageMapping;
import uncertain.schema.Collection;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import uncertain.schema.Schema;
import uncertain.schema.SchemaManager;

public class SchemaOCMTest extends BaseOCMTestCase {
    
    Schema              schema;
    SchemaManager       schemaManager;

    public SchemaOCMTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        schemaManager = new SchemaManager();
        mOcManager.getClassRegistry().addPackageMapping( new PackageMapping(Schema.NAMESPACE, Schema.class.getPackage().getName()) );
        ObjectRegistryImpl ori = new ObjectRegistryImpl();
        ori.registerInstance(ISchemaManager.class, schemaManager );
        mOcManager.setObjectCreator(ori);
        CompositeMap    map = super.mDocFactory.loadCompositeMap("uncertain.testcase.schema.Sample", "sxsd");
        schema = (Schema)super.mOcManager.createObject(map);
    }
    
    public void testSchemaOCM(){
        assertNotNull(schema);
        assertEquals(schema.getAttributes().length,2);
        assertEquals(schema.getTypes().length, 2);
        assertEquals(schema.getNameSpaces().length, 2);
        assertEquals(schema.getElements().length, 1);
        Element elm = schema.getElements()[0];
        assertEquals(elm.getCollections().length, 2);
        Collection col = elm.getCollections()[0];
        assertEquals(col.getMinOccur(), "0");
        assertEquals(col.getName(), "attributes");
        assertEquals(elm.getExtensions().length, 1);
        assertEquals(elm.getExtensions()[0].getBase(), "sc:complexType");
    }

}
