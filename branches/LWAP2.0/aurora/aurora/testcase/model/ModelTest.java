/*
 * Created on 2008-5-9
 */
package aurora.testcase.model;

import junit.framework.TestCase;
import uncertain.composite.CompositeMap;
import uncertain.core.UncertainEngine;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.ModelFactory;
import aurora.bm.Relation;

public class ModelTest extends TestCase {
    
    UncertainEngine     engine;
    ModelFactory        factory;

    public ModelTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        engine = new UncertainEngine();
        engine.initialize( new CompositeMap());
        factory = new ModelFactory(engine);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testMakeReady() throws Exception {
        BusinessModel model = factory.getModel("testcase.HR.EMP");
        assertNotNull(model);
        //model.makeReady();
        
        Field[] fields = model.getFields();
        assertNotNull(fields);
        assertEquals(fields.length, 12);
        assertNotNull(model.getField("ENAME"));
        Field[] f2 = model.getFields();
        assertTrue(fields==f2);
        Field[] pks = model.getPrimaryKeyFields();
        assertNotNull(pks);
        assertEquals(pks.length, 2);
        Relation[] relations = model.getRelations();
        assertNotNull(relations);
        assertEquals(relations.length, 2);
    }

}
