package org.lwap.plugin.ntlm;

import java.util.Iterator;
import java.util.Set;

import jcifs.Config;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

public class NtlmConfig {
	String procedure;
	String checksql;
	String checksession;
	String checksql_result;
	String checksession_result;
	CompositeMap domainInstances;
	IObjectRegistry mObjectRegistry;
	
	public NtlmConfig(IObjectRegistry registry) {
		mObjectRegistry=registry;
		Config.setProperty("jcifs.smb.client.soTimeout", "1800000");
		Config.setProperty("jcifs.netbios.cachePolicy", "1200");
		Config.setProperty("jcifs.smb.lmCompatibility", "0");
		Config.setProperty("jcifs.smb.client.useExtendedSecurity", "false");
	}
	
	public void onInitialize() {
		mObjectRegistry.registerInstance(NtlmConfig.class, this);		
	}
	
	public String getChecksql() {
		return checksql;
	}

	public void setChecksql(String checksql) {
		this.checksql = checksql;
	}

	public String getChecksession() {
		return checksession;
	}

	public void setChecksession(String checksession) {
		this.checksession = checksession;
	}

	public String getChecksql_result() {
		return checksql_result;
	}

	public void setChecksql_result(String checksql_result) {
		this.checksql_result = checksql_result;
	}

	public String getChecksession_result() {
		return checksession_result;
	}

	public void setChecksession_result(String checksession_result) {
		this.checksession_result = checksession_result;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}
	
	public DomainInstance getDomainInstance(String domain) {
		return (DomainInstance) domainInstances.get(domain);
	}	
	
	public DomainInstance getDefaultDomainInstance(){
		Set keySet=domainInstances.keySet();
		Iterator iterator =keySet.iterator();
		if(keySet.size()==1){
			while(iterator.hasNext()){
				return (DomainInstance) domainInstances.get(iterator.next());
			}
		}
		return null;
	}
	
	public void addInstances(DomainInstance[] domainInstance) {
		domainInstances = new CompositeMap();
		int l = domainInstance.length;
		DomainInstance intance = null;
		for (int i = 0; i < l; i++) {
			intance = domainInstance[i];
			domainInstances.put(intance.getDomain().toUpperCase(), intance);
		}
	}
}
