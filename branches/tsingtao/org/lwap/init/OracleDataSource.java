/**
 * Created on: 2002-11-21 12:40:09
 * Author:     zhoufan
 */
package org.lwap.init;


import uncertain.core.UncertainEngine;

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

public class OracleDataSource  {
    
    UncertainEngine	uncertainEngine;

    /*
    public OracleDataSource(UncertainEngine engine){
        uncertainEngine = engine;
    }

	OracleConnectionCacheImpl occi;
	
	String DBUrl;
	String DBUser;
	String DBPassword;
	int	   MaxConn=5;
	int	   MinConn=1;
	
	public String toString(){
	    return "OracleConnectionCacheImpl:DBUrl="+DBUrl+" DBUser="+DBUser+" DBPassword="+DBPassword;
	}


	   public void onInitialize() throws SQLException {
	       
	       	    if(DBUrl==null||DBUser==null||DBPassword==null)
	       	        throw new ConfigurationError("parameter not set correctly:"+toString());

	  			occi = new OracleConnectionCacheImpl();
				occi.setURL(DBUrl);		
				occi.setUser(DBUser);
				occi.setPassword(DBPassword);
				occi.setMaxLimit(MaxConn);
				occi.setMinLimit(MinConn);
				occi.setCacheScheme(OracleConnectionCacheImpl.FIXED_RETURN_NULL_SCHEME);
				uncertainEngine.getObjectSpace().registerParameter(DataSource.class, occi);

	}
	*/

}
