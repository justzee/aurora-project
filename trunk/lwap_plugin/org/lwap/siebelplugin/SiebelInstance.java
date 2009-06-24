/** 
 *  Hold configuration to connect to Siebel server
 *  Created on 2009-5-6
 */
package org.lwap.siebelplugin;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;

public class SiebelInstance implements IGlobalInstance {
    
	public static final String LOGGING_TOPIC = "org.lwap.siebelplug";
    public String CONNECTION_STRING;
    public String USER_NAME;
    public String PASSWORD;
    public String LANGUAGE;
    
    Logger        logger;
    
    private CompositeMap dataBeanMap = new CompositeMap();
    
//    private boolean inited = false;
    
    public SiebelInstance(){
        
    }
    
    public SiebelInstance(Logger logger){
        this.logger = logger;
    }
    
    public SiebelDataBean prepare() throws SiebelException{
    	SiebelDataBean	siebelDataBean = new SiebelDataBean();

            // login to the server
    	siebelDataBean.login(CONNECTION_STRING, USER_NAME, PASSWORD,LANGUAGE);            
        if(logger!=null) logger.info("Siebel connection  "+CONNECTION_STRING+" created");
        dataBeanMap.put(USER_NAME, siebelDataBean);
        return siebelDataBean;
    }
    public SiebelDataBean prepare(long time) throws SiebelException{
    	SiebelDataBean	siebelDataBean = new SiebelDataBean();

            // login to the server
    	siebelDataBean.login(CONNECTION_STRING, USER_NAME, PASSWORD,LANGUAGE);            
        if(logger!=null) logger.info("Siebel connection  "+CONNECTION_STRING+" created");
        dataBeanMap.put(USER_NAME+time, siebelDataBean);
        return siebelDataBean;
    }
    
    public void release() {
		Iterator keySetIterator = dataBeanMap.keySet().iterator();
		while (keySetIterator.hasNext()) {
			String userName = (String) keySetIterator.next();
			Object obj = dataBeanMap.get(userName);
			if (obj == null)
				continue;
			SiebelDataBean siebelDataBean = (SiebelDataBean) obj;

			try {
				if (siebelDataBean != null && siebelDataBean.logoff()) {
					if (logger != null)
						logger.info("Siebel connection  " + CONNECTION_STRING
								+ " released");
				}
			} catch (SiebelException e) {
				throw new RuntimeException("Siebel connection "
						+ CONNECTION_STRING + " can't be released");
			}
		}
		dataBeanMap.clear();
	}
    public void release(String User,long time){
    	Object obj = dataBeanMap.get(User+time);
    	SiebelDataBean siebelDataBean = null;
    	if(obj == null){
    		return;
    	}
    	siebelDataBean = (SiebelDataBean)obj;
    	try {
			if(siebelDataBean != null&&siebelDataBean.logoff()){
				if(logger!=null) logger.info("Siebel connection  "+CONNECTION_STRING+" released");
			}
		} catch (SiebelException e) {
			throw new RuntimeException("Siebel connection "+CONNECTION_STRING+" can't be released");
		}
        
    }
    public SiebelDataBean getClient() throws SiebelException {
    	//目前还没有好的方法可以测试是否已经断开连接
    	Object obj = dataBeanMap.get(USER_NAME);
    	SiebelDataBean dataBean = null;
    	if(obj != null){
    		dataBean = (SiebelDataBean)obj;
    		return dataBean;
    	}
    	dataBean = prepare();
        return dataBean;
    }
    public SiebelDataBean getClient(String User,String Pwd,long time) throws SiebelException {
    	//目前还没有好的方法可以测试是否已经断开连接
//    	Object obj = dataBeanMap.get(User);
    	SiebelDataBean dataBean = null;
//    	if(obj != null){
//    		dataBean = (SiebelDataBean)obj;
//    		return dataBean;
//    	}
    	USER_NAME = User;
    	PASSWORD = Pwd;
    	dataBean = prepare(time);
        return dataBean;
    }
    
    public void onShutdown(){
        release();
    }

}
