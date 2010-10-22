/*
 * Created on 2008-5-9
 */
package aurora.testcase.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.sql.DataSource;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingContext;
import aurora.bm.BusinessModel;
import aurora.bm.CascadeOperation;
import aurora.bm.Field;
import aurora.bm.ModelFactory;
import aurora.bm.Operation;
import aurora.bm.Relation;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.service.validation.IParameter;
import aurora.service.validation.IParameterIterator;
import aurora.testcase.database.AbstractModelServiceTest;
import aurora.testcase.database.DummyDataSource;

public class ModelTest extends AbstractModelServiceTest {
    
    static String PKG_NAME = ModelTest.class.getPackage().getName();
    
    ModelFactory        factory;
    DummyDataSource     ds;

    public ModelTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new ModelFactory(super.uncertainEngine.getOcManager());
        super.uncertainEngine.getObjectRegistry().registerInstanceOnce(DataSource.class, ds);        
    }

    
    public void testMakeReady() throws Exception {
        BusinessModel model = factory.getModel("testcase.HR.EMP");
        assertNotNull(model);
        //model.makeReady();
        
        Field[] fields = model.getFields();
        assertNotNull(fields);
        assertEquals(fields.length, 13);
        assertNotNull(model.getField("ENAME"));
        Field[] f2 = model.getFields();
        assertTrue(fields==f2);
        Field[] pks = model.getPrimaryKeyFields();
        assertNotNull(pks);
        assertEquals(pks.length, 1);
        Relation[] relations = model.getRelations();
        assertNotNull(relations);
        assertEquals(relations.length, 2);
        assertNull(model.getCascadeOperations());
        
        BusinessModel dept = factory.getModel("testcase.HR.DEPT");
        CascadeOperation[] ops = dept.getCascadeOperations();
        assertNotNull(ops);
        assertEquals(ops.length, 3);
        
        CascadeOperation op1 = ops[0];
        Set operations = op1.getEnabledOperations();
        assertNotNull(operations);
        assertEquals(operations.size(), 3);
        assertTrue(operations.contains("insert"));
        assertTrue(operations.contains("update"));
        assertTrue(operations.contains("delete"));
        
        CascadeOperation op2 = ops[1];
        operations = op2.getEnabledOperations();
        assertEquals(operations.size(), 1);
        
        CascadeOperation op3 = ops[2];
        operations = op3.getEnabledOperations();
        assertNull(operations);        
                
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
        //System.out.println(query_sql);
        
    }
    
    public void testDataFilter()
        throws Exception
    {
        String name = PKG_NAME+".wfl_workflow_notification";
        BusinessModelService bms = super.svcFactory.getModelService(name);
        bms.getServiceContext().getCurrentParameter().put("node_notification_id", "1");
        String sql = bms.getSql("delete").toString();
        assertTrue(sql.indexOf("rule_code<>'A'")>0);
        
    }
    
    public void testParameterForOperation()
        throws Exception
    {
        BusinessModel bm = super.svcFactory.getModelFactory().getModel(PKG_NAME+".wfl_workflow_notification");

        List lst1 = bm.getParameterForOperationInList("update");
        assertEquals(lst1.size(),2);
        
        List lst2 = bm.getParameterForOperationInList("insert");
        assertEquals(lst2.size(),13);
        
        List lst3 = bm.getParameterForOperationInList("delete");
        assertEquals(lst3.size(),1);

        BusinessModel bm2 = super.svcFactory.getModelFactory().getModel(PKG_NAME+".wfl_workflow_notification_rules");
        List lst4 = bm2.getParameterForOperationInList("update");
        assertEquals(lst4.size(), 7);
        
    }
    
    public void testGetSqlWithJoin() throws Exception {
        String name = PKG_NAME+".wfl_workflow_notification_for_test";
        BusinessModelService bms = super.svcFactory.getModelService(name);
        BusinessModelServiceContext context = bms.getServiceContext();
        LoggerProvider lp = LoggerProvider.createInstance(Level.FINE, System.out);
        LoggingContext.setLoggerProvider(context.getObjectContext(), lp);
        
        assertNotNull(bms);
        String sql = bms.getSql("query").toString();
        System.out.println("####### sql test ####################");
        System.out.println(sql);
              
    }
   
        
    protected void createConnection() throws Exception {
        ds = new DummyDataSource();
        conn = ds.getConnection();
    }
    

    

}
