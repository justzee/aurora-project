package org.lwap.sapplugin;

import java.util.HashMap;
import java.util.Map;

import uncertain.core.IGlobalInstance;

public class SapConfig implements IGlobalInstance{
	Map sapInstanceMap=new HashMap();
	String defaultKey=null;
	public SapInstance getSapInstance(String sid){
		return (SapInstance)sapInstanceMap.get(sid);
	}
	
	public SapInstance getDefaultSapInstance(){
		return (SapInstance)sapInstanceMap.get(defaultKey);
	}

	public void addInstances(SapInstance[] instances) {
		SapInstance instance;
		for(int i=0,l=instances.length;i<l;i++){
			instance=instances[i];
			if(defaultKey==null)
				defaultKey=instance.SID;
			sapInstanceMap.put(instance.SID, instance);
		}
	}
}
