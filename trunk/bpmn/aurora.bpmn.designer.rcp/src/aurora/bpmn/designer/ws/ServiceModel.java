package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.List;

public class ServiceModel {
	
	public static final String SERVICE_NAME = "service_name";
	public static final String HOST = "host";
	public static final String PSD = "psd";
	public static final String USER_NAME = "user_name";
	// Authorization
	private String userName = "abc";
	private String password = "cba";

	private String serviceName = "HEC BPM Service";
	
	private String host;

	private String saveServiceUrl ;
	private String listServiceUrl ;
	private String fetchServiceUrl ;
	private String deleteServiceUrl ;

	private boolean isLoaded;

	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();

	public String getSaveServiceUrl() {
		return Endpoints.getSaveService(host);
	}

	public void setSaveServiceUrl(String saveServiceUrl) {
		this.saveServiceUrl = saveServiceUrl;
	}

	public String getListServiceUrl() {
		return  Endpoints.getListService(host);
	}

	public void setListServiceUrl(String listServiceUrl) {
		this.listServiceUrl = listServiceUrl;
	}

	public String getFetchServiceUrl() {
		return  Endpoints.getFetchService(host);
	}

	public void setFetchServiceUrl(String fetchServiceUrl) {
		this.fetchServiceUrl = fetchServiceUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public List<BPMNDefineModel> getDefines() {
		return defines;
	}

	public void addDefine(BPMNDefineModel define) {
		if (define != null) {
			define.setServiceModel(this);
			this.defines.add(define);
		}
	}

	public void reload() {
		List<BPMNDefineModel> unSaveDefines = new ArrayList<BPMNDefineModel>();
		for (BPMNDefineModel define : defines) {
			if (define.getDefine_id() == null) {
				unSaveDefines.add(define);
			}
		}
		defines = new ArrayList<BPMNDefineModel>();
		defines.addAll(unSaveDefines);
	}

	public String getDeleteServiceUrl() {
		return  Endpoints.getDeleteService(host);
	}

	public void setDeleteServiceUrl(String deleteServiceUrl) {
		this.deleteServiceUrl = deleteServiceUrl;
	}

	public void removeDefine(BPMNDefineModel define) {
		defines.remove(define);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
