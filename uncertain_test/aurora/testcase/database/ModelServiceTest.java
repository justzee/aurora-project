/*
 * Created on 2008-5-8
 */
package aurora.testcase.database;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import aurora.database.FetchDescriptor;
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

}
