package org.lwap.kingdee;

import java.util.logging.Logger;


public class KingdeeInstance {
	 public String SERVER_IP;
	 public String SERVER_PORT;
	 
	 Logger        logger;
	    public static final String LOGGING_TOPIC = "org.lwap.kingdee";
	    public KingdeeInstance(){
	        
	    }
	    
	    public KingdeeInstance(Logger logger){
	        this.logger = logger;
	    }
	    public void prepare(){
	 
	    	System.out.println("sss");
	    }	    
}
