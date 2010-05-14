/*
 * Created on 2010-5-11 下午02:46:13
 * $Id$
 */
package aurora.testcase.database.profile;

import junit.framework.TestCase;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;
import aurora.database.local.oracle.sql.OracleInsertStatement;
import aurora.database.local.oracle.sql.ReturningIntoStatement;
import aurora.database.profile.DatabaseFactory;
import aurora.database.profile.IDatabaseProfile;
import aurora.database.profile.SqlBuilderRegistry;

public class DatabaseFactoryTest extends TestCase {
    
    OCManager           ocManager = OCManager.getInstance();
    DatabaseFactory     databaseFactory;
    CompositeLoader     loader;

    public DatabaseFactoryTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        PackageMapping pm = new PackageMapping(aurora.application.Namespace.AURORA_DATABASE_NAMESPACE, DatabaseFactory.class.getPackage().getName());
        ocManager.getClassRegistry().addPackageMapping(pm);
        loader = CompositeLoader.createInstanceForOCM();
        CompositeMap data = loader.loadFromClassPath(DatabaseFactoryTest.class.getPackage().getName()+".DatabaseFactory");
        assertNotNull(data);
        databaseFactory = (DatabaseFactory)ocManager.createObject(data);
        assertNotNull(databaseFactory);
    }
    
    public void testOCMValidation(){
        
        IDatabaseProfile ora10g = databaseFactory.getDatabaseProfile("Oracle10g");
        assertNotNull(ora10g);
        
        IDatabaseProfile mysql = databaseFactory.getDatabaseProfile("MySQL5");
        assertNotNull(mysql);
        
        IDatabaseProfile def = databaseFactory.getDefaultDatabaseProfile();
        assertTrue(def==ora10g);
        SqlBuilderRegistry oracle_reg = (SqlBuilderRegistry)ora10g.getSqlBuilderRegistry();
        assertNotNull(oracle_reg);
        assertNotNull(oracle_reg.getParent());
        assertTrue(databaseFactory.getDefaultSqlBuilderRegistry()==oracle_reg.getParent());
        
        OracleInsertStatement oist = new OracleInsertStatement("EMP");
        ReturningIntoStatement rt = new ReturningIntoStatement();
        rt.addField("ename", "${/out/@result}");
        rt.addField("empno", "${/out/@id}");
        oist.setReturningInto(rt);
        oist.addInsertField("ename", "${@ename}");
        String sql = ora10g.getSqlBuilderRegistry().getSql(oist);
        assertNotNull(sql);
        assertTrue(sql.indexOf("RETURNING ename,empno INTO ${/out/@result},${/out/@id}")>0);
    }

}
