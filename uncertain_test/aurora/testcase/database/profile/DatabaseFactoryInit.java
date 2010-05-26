/*
 * Created on 2010-5-26 下午03:12:48
 * $Id$
 */
package aurora.testcase.database.profile;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.ocm.OCManager;
import uncertain.ocm.PackageMapping;
import aurora.application.Namespace;
import aurora.database.profile.DatabaseFactory;

public class DatabaseFactoryInit {
    
    OCManager           ocManager = OCManager.getInstance();
    DatabaseFactory     databaseFactory;
    CompositeLoader     loader;
    
    
    public OCManager getOcManager() {
        return ocManager;
    }
    
    public DatabaseFactory getDatabaseFactory() {
        return databaseFactory;
    }
    
    public CompositeLoader getLoader() {
        return loader;
    }
    
    protected void setUp( String config_name ) throws Exception 
    {
        PackageMapping pm = new PackageMapping( Namespace.AURORA_DATABASE_NAMESPACE, DatabaseFactory.class.getPackage().getName());
        ocManager.getClassRegistry().addPackageMapping(pm);
        loader = CompositeLoader.createInstanceForOCM();
        String name = DatabaseFactoryTest.class.getPackage().getName()+"."+config_name;
        CompositeMap data = loader.loadFromClassPath(name);
        if(data==null)
            throw new IllegalArgumentException("Can't load "+name);
        databaseFactory = (DatabaseFactory)ocManager.createObject(data);
    }
    
    public DatabaseFactoryInit( String name )
        throws Exception
    {
        setUp(name);
    }
    

}
