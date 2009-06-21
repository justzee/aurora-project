/**
 * Created on: 2002-11-21 12:40:09
 * Author:     zhoufan
 */
package org.lwap.database.oracle;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleConnectionCacheImpl;

import org.lwap.application.Application;
import org.lwap.application.ApplicationInitializeException;
import org.lwap.application.ApplicationInitializer;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;

/**
 * Creates an oracle data-source from application config. Required configuration
 * should be put in <oracle-datasource> section
 * 
 * Sample configuration: <code>
	<oracle-datasource				db-url="jdbc:oracle:thin:@hr:1521:ORCL"
									db-user="hrms"
									db-password="hrms"
									max-conn="100"
									min-conn="2"
									name="named_connection">
	</oracle-datasource>	
 * </code>
 * 
 */

public class OracleDataSourceCreator implements ApplicationInitializer {

    public static final String KEY_ORACLE_DATASOURCE = "oracle-datasource";
    public static final String KEY_DB_URL = "db-url";
    public static final String KEY_DB_USER = "db-user";
    public static final String KEY_DB_PASSWORD = "db-password";
    public static final String KEY_MAX_CONN = "max-conn";
    public static final String KEY_MIN_CONN = "min-conn";
    public static final String KEY_USE_POOL = "use-pool";
    public static final String KEY_NAME = "name";

    OracleConnectionCacheImpl occi;
    SimpleDataSource simple_ds;

    CompositeMap app_config;
    boolean use_pool;

    String url;
    String user;
    String password;
    String name;

    List named_datasource_list;
    WebApplication application;

    /** default constructor */
    public OracleDataSourceCreator() {

    }

    public OracleDataSourceCreator(CompositeMap config) 
        throws ApplicationInitializeException
    {
        createDataSource(config);
    }

    protected void createDataSource(CompositeMap datasource_config)
            throws ApplicationInitializeException {

        name = datasource_config.getString(KEY_NAME);
        use_pool = datasource_config.getBoolean(KEY_USE_POOL, true);

        url = datasource_config.getString(KEY_DB_URL);
        user = datasource_config.getString(KEY_DB_USER);
        password = datasource_config.getString(KEY_DB_PASSWORD);
        if (use_pool) {
            System.out.println("Using pooled connection:" + url + ":" + user);
            try {
                occi = new OracleConnectionCacheImpl();
                occi.setURL(url);
                occi.setUser(user);
                occi.setPassword(password);
                occi.setMaxLimit(datasource_config.getInt(KEY_MAX_CONN, 10));
                occi.setMinLimit(datasource_config.getInt(KEY_MIN_CONN, 1));
                occi.setCacheScheme(OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME);
            } catch (SQLException ex) {
                throw new ApplicationInitializeException(ex);
            }
        } else {
            System.out.println("Using physical connection:" + url + ":" + user);
            simple_ds = new SimpleDataSource(url, user, password);
        }
    }

    protected void doApplicationInit() throws ApplicationInitializeException {
        Iterator it = app_config.getChildIterator();
        if (it == null){
            System.out.println("[warning] No data source configured");
            return;
        }
        while (it.hasNext()) {
            CompositeMap item = (CompositeMap) it.next();
            if (KEY_ORACLE_DATASOURCE.equalsIgnoreCase(item.getName())) {
                String name = item.getString(KEY_NAME);
                if (name == null) {
                    createDataSource(item);
                    application.setDataSource(getDataSource());
                    app_config.put(OracleDataSourceCreator.class.getName(),
                            this);
                } else {
                    OracleDataSourceCreator creator = new OracleDataSourceCreator(
                            item);
                    addDataSource(creator);
                }
            }
        }
    }

    public void addDataSource(OracleDataSourceCreator creator) {
        if (this.named_datasource_list == null)
            named_datasource_list = new LinkedList();
        named_datasource_list.add(creator);
        System.out.println("Adding named data source:"+creator.getName());
        this.app_config.put(creator.getName(), creator.getDataSource());
        /*
        application.getUncertainEngine().getGlobalContext().put(creator.getName(),
                getDataSource());
        */        
    }

    public DataSource getDataSource() {
        if (use_pool)
            return occi;
        else
            return simple_ds;
    }

    public String getName() {
        return name;
    }

    public void closeDataSource() {
        if (occi != null)
            try {
                occi.closeConnections();
                occi.close();
            } catch (Exception ex) {
            }
        occi = null;
        if( this.named_datasource_list!=null){
            Iterator it = this.named_datasource_list.iterator();
            while(it.hasNext()){
                OracleDataSourceCreator creator = (OracleDataSourceCreator)it.next();
                this.app_config.remove(creator.getName());
                creator.closeDataSource();
            }
        }
    }

    /**
     * @see org.lwap.application.ApplicationInitializer#initApplication(Application,
     *      CompositeMap)
     */
    public void initApplication(Application app, CompositeMap app_config)
            throws ApplicationInitializeException {
        this.app_config = app_config;
        this.application = (WebApplication) app;
        doApplicationInit();
    }

    public void cleanUp(Application app) {
        closeDataSource();
    }

    public void reload() throws ApplicationInitializeException {
        closeDataSource();
        createDataSource(app_config);
    }
    /*
     * public static void main(String[] args) throws Exception {
     * OracleConnectionCacheImpl occi; occi = new OracleConnectionCacheImpl();
     * occi.setURL("jdbc:oracle:thin:@192.168.11.54:1521:orawin");
     * occi.setUser("danysh"); occi.setPassword("123456");
     * 
     * occi.setMaxLimit(10); occi.setMinLimit(1);
     * occi.setCacheScheme(OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME);
     * 
     * Connection conn = occi.getConnection(); System.out.println(conn);
     * conn.close();
     * 
     * }
     */

}
