/*
 * Created on 2008-5-9
 */
package aurora.testcase.model;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingContext;

import aurora.bm.BusinessModel;
import aurora.bm.Field;
import aurora.bm.ModelFactory;
import aurora.bm.Operation;
import aurora.bm.Relation;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;
import aurora.testcase.database.AbstractModelServiceTest;

public class ModelTest extends AbstractModelServiceTest {
    
    static String PKG_NAME = ModelTest.class.getPackage().getName();
    
    ModelFactory        factory;

    public ModelTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new ModelFactory(super.uncertainEngine.getOcManager());
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
        assertEquals(pks.length, 1);
        Relation[] relations = model.getRelations();
        assertNotNull(relations);
        assertEquals(relations.length, 2);
    }
    
    public void testOperations()
        throws Exception
    {
        BusinessModel model = factory.getModel("testcase.HR.EMP_FOR_MAINTAIN");
        assertNotNull(model);
        Operation op = model.getOperation("query_for_test");
        assertNotNull(op);
        assertTrue(op.isQuery());
        String sql = op.getSql();
        assertTrue(sql.indexOf("select")>0);
        Operation op1 = model.getOperation("update");
        assertNotNull(op1);
        assertTrue(!op1.isQuery());
        assertTrue(op1.getSql().indexOf("update")>0);
    }
    
    void checkParameterExists( IParameterIterator pi, String[] expected_names){
        if(pi==null){
            assertNull(expected_names);
            return;
        }
        Set s = new HashSet();
        while(pi.hasNext()){
            
            IParameter pm = pi.next();
            s.add(pm.getName());
        }
        assertEquals(s.size(), expected_names.length);
        for(int i=0; i<expected_names.length; i++){
            assertTrue(s.contains(expected_names[i]));
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    public void testGetSql()
        throws Exception
    {
        String name = PKG_NAME+".wfl_workflow_notification";
        BusinessModelService bms = super.svcFactory.getModelService(name);
        BusinessModelServiceContext context = bms.getServiceContext();
        LoggerProvider lp = LoggerProvider.createInstance(Level.FINE, System.out);
        LoggingContext.setLoggerProvider(context.getObjectContext(), lp);
        
        assertNotNull(bms);
        
        StringBuffer sql = bms.getSql("query");
        assertNotNull(sql);
        
        BusinessModel bm = bms.getBusinessModel();
        Operation op = bm.getOperation("update");
        assertNotNull(op);
        assertNotNull(op.getSql());
        IParameterIterator pi =  bm.getParameterForOperation("update");
        assertNotNull(pi);
        checkParameterExists( pi, new String[]{"rule_code", "mail_template"} );
        
        context.getCurrentParameter().put("workflow_id", "1");
        StringBuffer update_sql = bms.getSql("update");
        assertNotNull(update_sql);
        assertTrue(update_sql.indexOf("mail_template=nvl(${@mail_template},'[empty]')")>0);
        assertTrue(update_sql.indexOf("rule_code is not null")>0);
        assertTrue(update_sql.indexOf("workflow_id = ${@workflow_id}")>0);
        //System.out.println(update_sql);
        
        context.getCurrentParameter().put("ORDER_FIELD", "node_notification_id");
        context.getCurrentParameter().put("ORDER_TYPE", "asc");
        StringBuffer query_sql = bms.getSql("queryForTest");
        assertTrue(query_sql.indexOf("rule_code is not null")>0);
        assertTrue(query_sql.indexOf("workflow_id = ${@workflow_id}")>0);
        System.out.println(query_sql);
        
    }
    
    
    
    

}
