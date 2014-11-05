package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.List;

public class ServiceModel {
	// Authorization
	private String userName = "abc";
	private String password = "cba";

	private String serviceName = "HEC BPM Service";

	private String saveServiceUrl = Endpoints.getSaveService();
	private String listServiceUrl = Endpoints.getListService();
	private String fetchServiceUrl = Endpoints.getFetchService();
	private String deleteServiceUrl = Endpoints.getDeleteService();

	private boolean isLoaded;

	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();

	public String getSaveServiceUrl() {
		return saveServiceUrl;
	}

	public void setSaveServiceUrl(String saveServiceUrl) {
		this.saveServiceUrl = saveServiceUrl;
	}

	public String getListServiceUrl() {
		return listServiceUrl;
	}

	public void setListServiceUrl(String listServiceUrl) {
		this.listServiceUrl = listServiceUrl;
	}

	public String getFetchServiceUrl() {
		return fetchServiceUrl;
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
		return deleteServiceUrl;
	}

	public void setDeleteServiceUrl(String deleteServiceUrl) {
		this.deleteServiceUrl = deleteServiceUrl;
	}

	public void removeDefine(BPMNDefineModel define) {
		defines.remove(define);
	}
}
