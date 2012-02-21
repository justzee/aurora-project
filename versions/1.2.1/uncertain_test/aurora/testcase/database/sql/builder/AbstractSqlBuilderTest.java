/*
 * Created on 2010-5-26 下午03:50:06
 * $Id$
 */
package aurora.testcase.database.sql.builder;

import junit.framework.TestCase;
import aurora.database.profile.DatabaseFactory;
import aurora.database.profile.IDatabaseProfile;
import aurora.database.profile.ISqlBuilderRegistry;
import aurora.testcase.database.profile.DatabaseFactoryInit;

public abstract class AbstractSqlBuilderTest extends TestCase {
    
    DatabaseFactory         mFactory;
    IDatabaseProfile        mProfile;
    ISqlBuilderRegistry     mRegistry;
    
    
    public static void assertContains(String s1, String s2){
        assertTrue(s1.indexOf(s2)>=0);
    }    

    public AbstractSqlBuilderTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        DatabaseFactoryInit di = new DatabaseFactoryInit("DatabaseFactory");
        mFactory = di.getDatabaseFactory();
        assertNotNull(mFactory);
        mProfile = mFactory.getDefaultDatabaseProfile();
        assertNotNull(mProfile);
        mRegistry = mProfile.getSqlBuilderRegistry();
        assertNotNull(mRegistry);
    }

}
