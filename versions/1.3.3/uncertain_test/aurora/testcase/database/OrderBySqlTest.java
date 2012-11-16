/*
 * Created on 2010-12-22 上午11:17:22
 * $Id$
 */
package aurora.testcase.database;

import uncertain.composite.CompositeMap;
import aurora.database.features.OrderByClauseCreator;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;

public class OrderBySqlTest extends AbstractModelServiceTest {

    public OrderBySqlTest(String name) {
        super(name);
    }
    
    public void testCreateQuerySql() throws Exception {
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();
        BusinessModelService service = svcFactory.getModelService(
                "testcase.HR.EMP", context);
        bc.getParameter().put(OrderByClauseCreator.ORDER_FIELD, "join_time");
        StringBuffer sql = service.getSql("query");
        int id = sql.indexOf("ORDER BY trunc(hiredate)");
        assertTrue(id>0);
        //System.out.println(sql);
    }

    protected void createConnection() throws Exception {
        DummyDataSource ds = new DummyDataSource();
        conn = ds.getConnection();
     }        
}
