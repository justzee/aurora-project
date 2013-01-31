/*
 * Created on 2010-8-26 下午05:13:20
 * $Id$
 */
package aurora.testcase.database;

import uncertain.composite.CompositeMap;
import aurora.database.profile.DatabaseProfile;
import aurora.database.profile.IDatabaseProfile;
import aurora.database.service.BusinessModelService;
import aurora.database.service.BusinessModelServiceContext;


public class JoinSqlTest extends AbstractModelServiceTest {
    
    static final String[]  CHECK_SQL_IN_JOIN = {
        "LEFT OUTER JOIN table2 cd ON c.code_id = cd.record_id",
        "RIGHT OUTER JOIN table3 dt ON c.desc_id = dt.record_id",
        "FULL OUTER JOIN table3 fk_desc2 ON c.desc_id = fk_desc2.record_id",
        "INNER JOIN table3 fk_desc3 ON c.long_desc_id = fk_desc3.record_id",
        "c.name not like 'D%'",
        "cd.code1 AS code1_name",
        "cd.code2 AS code2_name",
        "cd.code3 AS code3_name",
        "dt.desc2 AS desc2_name",
        "fk_desc3.desc1 AS long_desc_name"        
    };
    
    static final String[]  CHECK_SQL_IN_ORACLE = {
        "c.code_id = cd.record_id(+)",  
        "AND c.desc_id(+)  = dt.record_id",
        "AND c.desc_id(+)  = fk_desc2.record_id(+)",
        "AND c.long_desc_id = fk_desc3.record_id"
    };

    public JoinSqlTest(String name) {
        super(name);
    }
    
    public void testCreateSql() throws Exception {
        BusinessModelServiceContext bc = createContext();
        CompositeMap context = bc.getObjectContext();
        BusinessModelService service = svcFactory.getModelService(
                "aurora.testcase.model.join_test1", context);
        StringBuffer sql = service.getSql("query");
        for(int i=0; i<CHECK_SQL_IN_JOIN.length; i++)
            assertTrue(sql.indexOf(CHECK_SQL_IN_JOIN[i])>0);
        //System.out.println(sql);
        
        DatabaseProfile prof=(DatabaseProfile)databaseFactory.getDatabaseProfile(databaseFactory.getDefaultDatabase());
        prof.setProperty(IDatabaseProfile.KEY_USE_JOIN_KEYWORD, "false");        
        
        sql = service.getSql("query");
        
        //System.out.println(sql);        
        //assertNotNull(sql);
    }

    protected void createConnection() throws Exception {
       DummyDataSource ds = new DummyDataSource();
       conn = ds.getConnection();
    }    

}
