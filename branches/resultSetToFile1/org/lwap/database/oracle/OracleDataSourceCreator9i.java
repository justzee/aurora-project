/**
 * Created on: 2002-11-21 12:40:09
 * Author:     zhoufan
 */
package org.lwap.database.oracle;

import java.sql.SQLException;

import oracle.jdbc.pool.OracleConnectionCacheImpl;

import org.lwap.application.Application;
import org.lwap.application.ApplicationInitializeException;
import org.lwap.application.ApplicationInitializer;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;

/**
 * Creates an oracle data-source from application config.
 * Required configuration should be put in <oracle-datasource> section
 *
 * Sample configuration:
 * <code>
	<oracle-datasource				db-url="jdbc:oracle:thin:@hr:1521:ORCL"
									db-user="hrms"
									db-password="hrms"
									max-conn="100"
									min-conn="2">
	</oracle-datasource>	
 * </code>
 * 
 */

public class OracleDataSourceCreator9i implements ApplicationInitializer {

  	public static final String KEY_ORACLE_DATASOURCE = "oracle-datasource";
  	public static final String KEY_DB_URL = "db-url";
  	public static final String KEY_DB_USER = "db-user";
  	public static final String KEY_DB_PASSWORD = "db-password";  
  	public static final String KEY_MAX_CONN = "max-conn";
  	public static final String KEY_MIN_CONN = "min-conn";  

	OracleConnectionCacheImpl      occi;
    CompositeMap                   app_config;
    
    public void createDataSource( CompositeMap app_config )
        throws ApplicationInitializeException
    {
            CompositeMap datasource_config = app_config.getChild(KEY_ORACLE_DATASOURCE);
            if( datasource_config == null) throw new ApplicationInitializeException("OracleDataSourceCreator:can't find <oralce-datasource> section in application config");
            try{
                occi = new OracleConnectionCacheImpl();
                occi.setURL(datasource_config.getString(KEY_DB_URL));       
                occi.setUser(datasource_config.getString(KEY_DB_USER));
                occi.setPassword(datasource_config.getString(KEY_DB_PASSWORD));
                occi.setMaxLimit(datasource_config.getInt(KEY_MAX_CONN,10));
                occi.setMinLimit(datasource_config.getInt(KEY_MIN_CONN,1));
                occi.setCacheScheme(OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME);
            } catch(SQLException ex){
                throw new ApplicationInitializeException(ex);
            } 
    }
    
    public void closeDataSource(){
        try{
            occi.closeConnections();
            occi.close();
        } catch(Exception ex){
        }
        occi = null;
    }


	/**
	 * @see org.lwap.application.ApplicationInitializer#initApplication(Application, CompositeMap)
	 */
	public void initApplication(Application app, CompositeMap app_config)
		throws ApplicationInitializeException {
        this.app_config = app_config;
        createDataSource(app_config);
        WebApplication webapp = (WebApplication)app; 
        webapp.setDataSource(occi);
        app_config.put(OracleDataSourceCreator9i.class.getName(), this);
        //webapp.getUncertainEngine().getObjectSpace().registerParamOnce(OracleDataSourceCreator.class, this);
	}
	
	public void cleanUp(Application app){
	    closeDataSource();
	}
    
    public void reload()
        throws ApplicationInitializeException
    {
        closeDataSource();
        createDataSource(app_config);
    }
/*	
	public static void main(String[] args) throws Exception {
				OracleConnectionCacheImpl occi;
	  			occi = new OracleConnectionCacheImpl();
				occi.setURL("jdbc:oracle:thin:@192.168.11.54:1521:orawin");		
				occi.setUser("danysh");
				occi.setPassword("123456");
				
				occi.setMaxLimit(10);
				occi.setMinLimit(1);
				occi.setCacheScheme(OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME);
				
				Connection conn = occi.getConnection();	
				System.out.println(conn);
				conn.close();		
		
	}
*/	

}
