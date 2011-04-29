package org.lwap.database.c3p0;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.sql.DataSource;

import org.lwap.application.Application;
import org.lwap.application.ApplicationInitializeException;
import org.lwap.application.ApplicationInitializer;
import org.lwap.application.WebApplication;
import org.lwap.database.ConnectionInitializer;
import org.lwap.database.IConnectionInitializer;
import org.lwap.database.oracle.OracleDataSourceCreator;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import uncertain.composite.CompositeMap;

public class DataSourceCreater implements ApplicationInitializer{
	public static final String KEY_DATASOURCE = "database-connection";
	public static final String KEY_PROPERTIES="properties";
	public static final String KEY_NAME = "name";
    public static final String KEY_INIT_SQL = "init-sql";
    
	CompositeMap poolConfig=null;
	CompositeMap app_config;
	WebApplication application;
	IConnectionInitializer connection_initializer;
	public void initApplication(Application app, CompositeMap app_config)
			throws ApplicationInitializeException {
		this.application = (WebApplication) app;
		this.app_config=app_config;
		try {
			doApplicationInit();
		} catch (Exception e){		
			e.printStackTrace();
		}
	}

	public void cleanUp(Application app) {
		// TODO Auto-generated method stub
		
	}
	
	public void addProperties(CompositeMap config) throws Exception{
		String key;
		String text=config.getText();		
		Properties properties=new Properties();		
				
		ByteArrayInputStream stream = new ByteArrayInputStream(text.getBytes("UTF-8"));			
		properties.load(stream);
		
		Enumeration enumn=properties.propertyNames();
		if(enumn!=null){
			this.poolConfig=new CompositeMap();	
			while (enumn.hasMoreElements()) {
				key = (String) enumn.nextElement();
				this.poolConfig.put(key, properties.getProperty(key).trim());
			}
		}
	}
	void doApplicationInit() throws Exception{
		Iterator it=app_config.getChildIterator();
		if (it == null){
            System.out.println("[error] No application configured");
            return;
        }
		while(it.hasNext()){
			CompositeMap item = (CompositeMap) it.next();
			if (KEY_DATASOURCE.equalsIgnoreCase(item.getName())) {
				CompositeMap config=item.getChild(KEY_PROPERTIES);
				addProperties(config);
                String name = item.getString(KEY_NAME);                                
                application.setDataSource(createDataSource(item));
                if(connection_initializer!=null){
                    System.out.println("connection initializer:"+connection_initializer);
                    application.setConnectionInitializer(connection_initializer);
                }else{
                    System.out.println("No connection initializer configured");
                }               
            }
		}
	}
	DataSource createDataSource(CompositeMap dbConfig) throws SQLException{		
		DataSource ds=null;		
		ds=DataSources.unpooledDataSource(dbConfig.getString("url"),dbConfig.getString("userName"),dbConfig.getString("password"));
		((DriverManagerDataSource)ds).setDriverClass(dbConfig.getString("driverClass"));		
		if(dbConfig.getBoolean("pool",false)&&this.poolConfig!=null){		
			ds=DataSources.pooledDataSource(ds, poolConfig);				
		}
		String init_sql = dbConfig.getString(KEY_INIT_SQL);
        if(init_sql!=null){
            connection_initializer = new ConnectionInitializer(init_sql);
        } 
		return ds;
	}
	
}
