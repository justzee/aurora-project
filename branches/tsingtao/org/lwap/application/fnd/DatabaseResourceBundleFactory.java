/*
 * Created on 2005-10-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.lwap.application.ResourceBundleFactory;
import org.lwap.database.DBUtil;
import org.lwap.database.ResultSetLoader;

import uncertain.composite.CompositeMap;
/**
 * @author Jian
 *
 */
public class DatabaseResourceBundleFactory implements ResourceBundleFactory {

    DefaultResourceBundle resourceBundle = new DefaultResourceBundle();
    CompositeMap service_map = new CompositeMap("service");
    // String -> Locale
    Map         mLocales = new HashMap();    
    
    public DatabaseResourceBundleFactory( CompositeMap cfg, Connection cn) 
        throws SQLException
    {
        loadResource(cfg, cn);
    }


    /****
     * load Resource fron table,put into HashMap
     * HashMap.put(Locale,HRResourceBundle)
     * @exception  ResourceNotFoundException
     * @param cn  Connection
    */
    public void loadResource( CompositeMap cfg, Connection cn)
        throws SQLException
    {
        PreparedStatement      pst = null;
        ResultSet        rsContent = null;
        //int               localeId = 0;
        
        String prompt_table_name = cfg.getString("prompt_table", "fnd_prompt");
        String service_table_name = cfg.getString("service_table", "fnd_service");
        
        String strSqlQueryContent  = "select t.code,t.prompt from  " + prompt_table_name + " t";
        String strSqlService = "select * from " + service_table_name + " t ";
        if (cn == null) {
            System.err.println("connection is null");
            return;
        }

        try{
                pst = cn.prepareStatement(strSqlQueryContent);
                rsContent = pst.executeQuery();
                
                while (rsContent.next()){
                    String key = rsContent.getString(1);
                    String value = rsContent.getString(2);
                    //System.out.println("first:"+value);
//                    if (value != null)
//                        value = new String(value.getBytes("ISO8859_1"), "GBK");
                    //System.out.println("end:"+value);
                    resourceBundle.putString(key,value);
                }
                rsContent.close();
 
                pst = cn.prepareStatement(strSqlService);
                rsContent = pst.executeQuery();
                ResultSetLoader rl = new ResultSetLoader(rsContent);
                CompositeMap root = new CompositeMap("root");
                rl.loadList(root,"service-config", rsContent);
                Iterator it = root.getChildIterator();
                if(it!=null)
                while (it.hasNext()){
                    CompositeMap config = (CompositeMap)it.next();
                    String key = config.getString("SERVICE_NAME");
                    key = key.toLowerCase();
                    service_map.put(key, config);
                }
        }finally{
              DBUtil.closeResultSet(rsContent);
              DBUtil.closeStatement(pst);
        }
    }

    /*****
     * get HRResourceBundle from hashMap
     * @param  locale    Locale
     * @return  ResourceBundle
     * @exception  ResourceNotFoundException
     */
    public ResourceBundle getResourceBundle(Locale locale) {
         return resourceBundle;
    }
    
    public CompositeMap getServiceMap() {
        return service_map;
    }
    
    public Locale  getLocale( String code ){
        Locale l = (Locale)mLocales.get(code);
        if(l==null){
            l = new Locale(code);
            mLocales.put(code, l);
        }
        return l;
    }    
/*
	public static void main(String[] args) throws Exception {

	   DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
       Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.11.3:1522:demo10","fsms","fsms");

       FSMSResourceBundleFactory fact = new FSMSResourceBundleFactory( conn);
       ResourceBundle fb = fact.getResourceBundle(null);
       CompositeMap c= fact.getServiceMap();
       Enumeration e = fb.getKeys();
       while (e.hasMoreElements()) {
           System.out.println(fb.getString((String)e.nextElement()));
       }
       System.out.println(c.toXML());

	}
*/
}
