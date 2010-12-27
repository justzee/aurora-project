package org.lwap.database.oracle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.lwap.application.Application;
import org.lwap.application.ApplicationInitializeException;
import org.lwap.application.ApplicationInitializer;
import org.lwap.application.WebApplication;
import org.lwap.database.ConnectionInitializer;
import org.lwap.database.IConnectionInitializer;

import uncertain.composite.CompositeMap;

public class JndiDataSourceCreator implements ApplicationInitializer{
	WebApplication application;
	IConnectionInitializer connection_initializer;
	public static final String KEY_JNDI_DATASOURCE = "jndi-datasource";
	public static final String KEY_NAME ="name";
    public static final String KEY_INIT_SQL = "init-sql";
    private String jndiname ;
	public void cleanUp(Application app) {
		
	}

	public void initApplication(Application app, CompositeMap appConfig)
			throws ApplicationInitializeException {
		try {
			CompositeMap config = appConfig.getChild(KEY_JNDI_DATASOURCE);
			connection_initializer = new ConnectionInitializer(config.getString(KEY_INIT_SQL));
			jndiname=config.getString(KEY_NAME);
			application =(WebApplication)app;
			application.setDataSource(getDataSource());
			application.setConnectionInitializer(connection_initializer);
		} catch (Exception e) {
			 throw new ApplicationInitializeException();
		}
	}
	public JndiDataSourceCreator() {

    }
	 public DataSource getDataSource() throws Exception {
		 String jndi_name="java:comp/env/"+jndiname; 
		 Context ctx = new InitialContext(); 
		 if(ctx == null ) 
			 throw new Exception("no context"); 
			 DataSource ds = (DataSource)ctx.lookup(jndi_name); 
			 return ds;
	    } 
}
