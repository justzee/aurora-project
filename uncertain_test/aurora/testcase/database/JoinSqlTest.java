/*
 * Created on 2010-8-26 下午05:13:20
 * $Id$
 */
package aurora.testcase.database;

import uncertain.composite.CompositeMap;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;


public class JoinSqlTest extends AbstractModelServiceTest {

    public JoinSqlTest(String name) {
        super(name);
    }
    
    public void testCreateSql() throws Exception {
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();
        BusinessModelService service = svcFactory.getModelService(
                "aurora.testcase.model.join_test1", context);
        StringBuffer sql = service.getSql("query");
        System.out.println(sql);
        //assertNotNull(sql);
    }

    protected void createConnection() throws Exception {
       DummyDataSource ds = new DummyDataSource();
       conn = ds.getConnection();
    }    

}
