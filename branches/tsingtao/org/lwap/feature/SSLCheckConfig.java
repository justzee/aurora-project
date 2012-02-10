package org.lwap.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uncertain.composite.CompositeMap;
import uncertain.core.IGlobalInstance;

public class SSLCheckConfig implements IGlobalInstance {
	
	private static final int DEFAULT_HTTPS_PORT = 8443;
	
	private List list = new ArrayList();
	
	private int httpsPort = DEFAULT_HTTPS_PORT;
	
	
    
    public CompositeMap getServices(){
        return null;
    }
    
    public void addServices(CompositeMap childs){
    	if(childs!=null){
    		Iterator it = childs.getChildIterator();
    		if(it!=null){
    			while(it.hasNext()){
    				CompositeMap service = (CompositeMap)it.next();
    				String name = service.getString("name");
    				list.add(name.toLowerCase());
    			}
    		}
    	}
    }
    
    public List getAllServices(){
        return this.list;
    }

	public int getHttpsPort() {
		return httpsPort;
	}

	public void setHttpsPort(int httpsPort) {
		this.httpsPort = httpsPort;
	}
}
