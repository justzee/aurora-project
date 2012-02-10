/*
 * Created on 2011-9-26 上午10:39:46
 * $Id$
 */
package uncertain.testcase.core;

import java.io.File;
import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.TestCase;
import uncertain.composite.QualifiedName;
import uncertain.core.DirectoryConfig;
import uncertain.core.UncertainEngine;
import uncertain.schema.Element;
import uncertain.schema.ISchemaManager;
import aurora.application.AuroraApplication;

public class TestEngineStartup extends TestCase {
    
    UncertainEngine     uncertainEngine;
    File base_path;
    File config_path;


    public TestEngineStartup(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        base_path =  new File("/Users/zhoufan/Work/workspace/HEC2.0/web");
        config_path = new File(base_path,"WEB-INF");
        assertTrue(base_path.exists());
        uncertainEngine = new UncertainEngine();
        DirectoryConfig dirConfig = uncertainEngine.getDirectoryConfig();
        dirConfig.setBaseDirectory(base_path.getCanonicalPath());
        dirConfig.setConfigDirectory(config_path.getCanonicalPath());
        uncertainEngine.getPackageManager().loadPackageFromRootClassPath("aurora_builtin_package");

        uncertainEngine.startup(false);

    }
    
    public void testLoadDataSource()
        throws Exception
    {
        
        File db_config_file = new File(config_path,"0.datasource.config");
        assertTrue(db_config_file.exists());
        uncertainEngine.loadConfigFile(db_config_file.getCanonicalPath());
        
        DataSource ds = (DataSource)(uncertainEngine.getObjectRegistry().getInstanceOfType(DataSource.class));
        assertNotNull(ds);
        Connection conn = ds.getConnection();
        assertNotNull(conn);
        conn.close();        
    }
    
    public void testSchema(){
        ISchemaManager sm = uncertainEngine.getSchemaManager();
        Element elm = sm.getElement( new QualifiedName(AuroraApplication.AURORA_FRAMEWORK_NAMESPACE, "screen") );
        assertNotNull(elm);
    }
/*
    protected void tearDown() throws Exception {
        uncertainEngine.shutdown();
        super.tearDown();
    }
 */   
    

}
