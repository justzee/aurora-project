package org.lwap.sapplugin;

import java.util.HashMap;
import java.util.Map;

import uncertain.core.IGlobalInstance;

public class SapConfig implements IGlobalInstance{
	Map sapInstanceMap=new HashMap();
	SapInstance defaultSapInstance;
	public SapInstance getSapInstance(String sid){
		return (SapInstance)sapInstanceMap.get(sid);
	}
	
	public SapInstance getSapInstance(){
		return defaultSapInstance;
	}
	
	public void addInstances(SapInstance[] instances) {
		SapInstance instance;
		int l=instances.length;
		for(int i=0;i<l;i++){
			instance=instances[i];		
			sapInstanceMap.put(instance.SID, instance);
		}
		if(l==1)
			defaultSapInstance=instances[0];
	}
}
