/*
 * Created on 2010-8-2 下午09:16:05
 * $Id$
 */
package aurora.testcase.model;

import junit.framework.TestCase;
import uncertain.ocm.OCManager;
import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.ModelFactory;
import aurora.bm.Relation;

public class ModelFactoryTest extends TestCase {
    
    static final String PKG_NAME = ModelFactoryTest.class.getPackage().getName();
    
    ModelFactory        fact;

    public ModelFactoryTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        fact = new ModelFactory(OCManager.getInstance());
    }
    
    public void testModelExtend()
        throws Exception
    {
        BusinessModel child1 = fact.getModel(PKG_NAME+".child1");
        assertNotNull(child1);
        assertNotNull(child1.getField("new_field"));
        Field ename = child1.getField("ename");
        assertTrue(!ename.isForDisplay());
        //System.out.println(child1.getObjectContext().toXML());
        
        BusinessModel child2 = fact.getModel(PKG_NAME+".child2");
        assertNotNull(child2);
        Field level = child2.getField("level");
        assertNotNull(level);
        assertEquals("java.lang.Long", level.getDataType());
        assertNotNull(child2.getField("new_field"));
        Relation dept = child2.getRelation("DEPT");
        assertNotNull(dept);
        assertEquals("LEFT OUTTER", dept.getJoinType());
    }

}
