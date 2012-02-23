/*
 * Created on 2008-5-8
 */
package aurora.testcase.database;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import aurora.database.FetchDescriptor;
import aurora.database.actions.ModelBatchUpdate;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;
import aurora.service.json.JSONDirectOutputor;

public class ModelServiceTest extends AbstractModelServiceTest {

    public ModelServiceTest(String arg0) {
        super(arg0);

    }

    protected void initConfiguration(Configuration config) {
        config.addParticipant(this);
    }

    public void testUpdateAndQuery() throws Throwable {
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();

        long time = System.currentTimeMillis();
        BusinessModelService service = svcFactory.getModelService(
                "testcase.HR.EMP", context);
        time = System.currentTimeMillis() - time;
        // System.out.println("[load time] "+time);

        // service.getServiceContext().setConnection(conn);
        // service.getServiceContext().setTrace(true);
        Map parameters = new HashMap();
        Long id = new Long("7499");
        String new_name = "ALLEN Updated";
        parameters.put("empno", id);
        parameters.put("ename", new_name);
        parameters.put("deptno", new Long(20));
        parameters.put("mgr", new Long(7839));
        parameters.put("hiredate", new java.sql.Date(new Date().getTime()));
        service.updateByPK(parameters);
        // assertNotNull(context.get("update_invoked"));
        // assertEquals(bc.getConfig(), rootConfig);
        conn.commit();

        parameters.clear();
        parameters.put("deptno", new Long(20));
        parameters.put("mgr", new Long(2222));
        parameters.put("mgr_name", "JONES");
        FetchDescriptor desc = FetchDescriptor.createInstance(0, 50);
        CompositeMap result = service.queryAsMap(parameters, desc);
        assertNotNull(result);
        Iterator it = result.getChildIterator();
        assertNotNull(it);
        CompositeMap record = null;
        while (it.hasNext()) {
            record = (CompositeMap) it.next();
            if (id.equals(record.getLong("empno")))
                break;
            else
                record = null;
        }
        assertNotNull(record);
        assertEquals(new_name, record.getString("ename"));
        //System.out.println(result.toXML());

        JSONDirectOutputor consumer = new JSONDirectOutputor(new PrintWriter(
                System.out));
        service.query(parameters, consumer, desc);

        // System.out.println(context.toXML());
    }

    public void testInsert() throws Exception {
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();
        // LoggingContext.setLogger(context, uncertainEngine.getLogger(topic));
        BusinessModelService service = svcFactory.getModelService(
                "testcase.HR.EMP", context);
        Map emp = new HashMap();
        emp.put("ename", "new employee");
        emp.put("deptno", new Long(10));
        service.insert(emp);
        //System.out.println(context.toXML());
        conn.commit();
        CompositeMap data = service.queryAsMap(emp);
        assertNotNull(data);
        //System.out.println(data.toXML());
    }

    public void testCreateSql() throws Exception {
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();
        BusinessModelService service = svcFactory.getModelService(
                "testcase.HR.EMP", context);
        StringBuffer sql = service.getSql("Insert");
        assertNotNull(sql);
    }

    public void onExecuteUpdate(BusinessModelServiceContext context) {
        context.putBoolean("update_invoked", true);
    }
    
    public void testBatchInsert()
        throws Exception
    {
        conn.setAutoCommit(false);
        
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();
        
        BusinessModelService s1 = svcFactory.getModelService("testcase.HR.ClearTestData", context);
        s1.execute(null);

        BusinessModelService service = svcFactory.getModelService(
                "testcase.HR.DEPT_FOR_BATCH", context);
        
        CompositeLoader loader = CompositeLoader.createInstanceWithExt("xml");
        CompositeMap data = loader.loadFromClassPath("testcase.HR.dept_batch_insert");
        assertNotNull(data);
        CompositeMap depts = data.getChild("dept-list");
        assertNotNull(depts);
        bc.getParameter().addChild(depts);
        
        ModelBatchUpdate mbu = new ModelBatchUpdate(svcFactory, uncertainEngine.getOcManager(), uncertainEngine.getObjectRegistry() );
        mbu.setModel("testcase.HR.DEPT_FOR_BATCH");
        //mbu.setSourcePath("");
        mbu.doBatchUpdate(depts.getChilds(), context);
        
        conn.commit();
        
        BusinessModelService empsvc = svcFactory.getModelService(
                "testcase.HR.EMP", context);
        Map params = new HashMap();
        params.put("ename", "EMP_IN_BATCH_MODE%");
        params.put("deptno", new Long(0));
        CompositeMap records = empsvc.queryAsMap(params);        
        assertEquals(records.getChilds().size(), 2);
        
        params.put("deptno", new Long(1));
        records = empsvc.queryAsMap(params);
        assertEquals(records.getChilds().size(), 3);
        
        
    }

}
