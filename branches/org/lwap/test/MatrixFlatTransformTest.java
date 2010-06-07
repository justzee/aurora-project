/*
 * Created on 2009-3-27
 */
package org.lwap.test;

import org.lwap.action.MatrixFlatTransform;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.proc.ProcedureRunner;

public class MatrixFlatTransformTest extends TestCase {
    
    MatrixFlatTransform mtf;
    ProcedureRunner runner;
    CompositeMap context;

    public MatrixFlatTransformTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        CompositeLoader loader = new CompositeLoader();
        context = loader.loadFromClassPath("org.lwap.test.MatrixFlatTransformTest");
        runner = new ProcedureRunner();
        runner.setContext(context);
        
        mtf = new MatrixFlatTransform();
        mtf.Column_config_path = "/model/item-list";
        mtf.Column_field_name = "name";
        mtf.Source_data_path = "/model/data";
        
    }
    
    public void testTransform() throws Exception {
        mtf.run(runner);
        CompositeMap data = (CompositeMap)context.getObject("/model/data");
        assertEquals(data.getChilds().size(), 3);
        CompositeMap record = (CompositeMap)data.getChilds().get(0);
        assertEquals(record.getChilds().size(), 3);
        CompositeMap sub_record = (CompositeMap)record.getChilds().get(2);
        assertEquals(sub_record.get("value"), "150");
    }

}
