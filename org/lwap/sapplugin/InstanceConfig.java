package org.lwap.sapplugin;

import java.util.logging.Logger;

import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

public class InstanceConfig {
public static final String LOGGING_TOPIC = "org.lwap.pluin.sap";
    
    public String SID;
    public String USERID;
    public String PASSWORD;
    public String SERVER_IP;
    public String DEFAULT_LANG;
    public int    MAX_CONN;
    public String SAP_CLIENT;
    public String SYSTEM_NUMBER;
    
    Logger        logger;
    
    IRepository     repository;
    
    private boolean inited = false;
    
    public InstanceConfig(){
        
    }
    
    public InstanceConfig(Logger logger){
        this.logger = logger;
    }
    
    public void prepare(){
        if(!inited){
            JCO.addClientPool(
                    SID,          // Alias for this pool
                    MAX_CONN,     // Max. number of connections
                    SAP_CLIENT,   // SAP client
                    USERID,       // userid
                    PASSWORD,     // password
                    DEFAULT_LANG,                     // language
                    SERVER_IP,    // host name
                   SYSTEM_NUMBER );
            repository = JCO.createRepository("MYRepository", SID);            
            if(logger!=null) logger.info("SAP connection pool "+SID+" created");
            inited = true;
        }
    }
    
    public IRepository getRepository(){
        if(!inited)
            prepare();
        if(repository==null) throw new RuntimeException("SAP connection pool can't be created");
        return repository;
    }
    
    public void release(){
        JCO.removeClientPool(SID);
        if(logger!=null) logger.info("SAP connection pool "+SID+" released");
        inited = false;
    }
    
    //private boolean inited = false;
    public JCO.Client getClient() {
        if(!inited)
            prepare();
        JCO.Client client = JCO.getClientPoolManager().getClient(SID);        
        return client;
    }
    
    public void onShutdown(){
        release();
    }
    
	public String getSid() {
		return SID;
	}

	public void setSid(String sid) {
		this.SID = sid;
	}

	public String getUserid() {
		return USERID;
	}

	public void setUserid(String userid) {
		this.USERID = userid;
	}

	public String getPassword() {
		return PASSWORD;
	}

	public void setPassword(String password) {
		this.PASSWORD = password;
	}

	public String getServer_ip() {
		return SERVER_IP;
	}

	public void setServer_ip(String server_ip) {
		this.SERVER_IP = server_ip;
	}

	public String getDefault_lang() {
		return DEFAULT_LANG;
	}

	public void setDefault_lang(String default_lang) {
		this.DEFAULT_LANG = default_lang;
	}

	public int getMax_conn() {
		return MAX_CONN;
	}

	public void setMax_conn(int max_conn) {
		this.MAX_CONN = max_conn;
	}

	public String getSap_client() {
		return SAP_CLIENT;
	}

	public void setSap_client(String sap_client) {
		this.SAP_CLIENT = sap_client;
	}

	public String getSystem_number() {
		return SYSTEM_NUMBER;
	}

	public void setSystem_number(String system_number) {
		this.SYSTEM_NUMBER = system_number;
	} 
}
