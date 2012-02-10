/*
 * Created on 2005-10-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.lwap.application.Application;
import org.lwap.application.ApplicationInitializeException;
import org.lwap.application.ResourceBundleFactory;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;

/**
 * @author Jian
 *
 */
public class ApplicationInitializer implements
        org.lwap.application.ApplicationInitializer {
    
    public final static String SERVICE_MAP = "service_map"; 

    /* (non-Javadoc)
     * @see org.lwap.application.ApplicationInitializer#initApplication(org.lwap.application.Application, uncertain.composite.CompositeMap)
     */
    public void initApplication(Application app, CompositeMap app_config)
	throws ApplicationInitializeException {
		
		Connection conn = null;
		WebApplication webapp = (WebApplication)app;
		try{
			DataSource ds = webapp.getDataSource();
			if( ds == null) throw new ApplicationInitializeException("ApplicationInitializer:Can't get DataSource from app");				
			conn = ds.getConnection();
			DatabaseResourceBundleFactory fact = new DatabaseResourceBundleFactory(webapp.getApplicationConfig(),  conn);
            // set resource bundle factory
			{
    			ResourceBundleFactory existing_fact = webapp.getResourceBundleFactory();
    			if( existing_fact==null || existing_fact instanceof DatabaseResourceBundleFactory){
        			webapp.setResourceBundleFactory(fact);
    			}
			}
			// reload service map
            webapp.getApplicationConfig().put(SERVICE_MAP,fact.getServiceMap());
		} catch(SQLException ex){
			throw new ApplicationInitializeException(ex);
		} finally{
			try{
				if( conn != null) conn.close();
			}catch(SQLException ex){
			}
		}
		
}

    /* (non-Javadoc)
     * @see org.lwap.application.ApplicationInitializer#cleanUp(org.lwap.application.Application)
     */
    public void cleanUp(Application app) {
        // TODO Auto-generated method stub

    }

}
