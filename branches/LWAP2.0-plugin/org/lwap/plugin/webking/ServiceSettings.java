package org.lwap.plugin.webking;

import uncertain.core.IGlobalInstance;

public class ServiceSettings implements IGlobalInstance {
	public ServiceSettings() {
	}

	private static final String DEFAULT_DEST_IP = "localhost";
	private static final int DEFAULT_DEST_PORT = 5286;
	private String serviceIP = DEFAULT_DEST_IP;
	private int servicePORT = DEFAULT_DEST_PORT;

	public String getServiceIP() {
		return serviceIP;
	}

	public void setServiceIP(String serviceIP) {
		this.serviceIP = serviceIP;
	}

	public int getServicePORT() {
		return servicePORT;
	}

	public void setServicePORT(int servicePORT) {
		this.servicePORT = servicePORT;
	}

}
