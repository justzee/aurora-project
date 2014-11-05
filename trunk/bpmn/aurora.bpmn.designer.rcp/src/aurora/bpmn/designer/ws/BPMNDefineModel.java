package aurora.bpmn.designer.ws;

public class BPMNDefineModel {

	private String name;
	private String define_id;
	private String process_code;
	private String process_version;
	private String current_version_flag;
	private String defines;
	private String description;
	private ServiceModel serviceModel;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefine_id() {
		return define_id;
	}

	public void setDefine_id(String define_id) {
		this.define_id = define_id;
	}

	public String getProcess_code() {
		return process_code;
	}

	public void setProcess_code(String process_code) {
		this.process_code = process_code;
	}

	public String getProcess_version() {
		return process_version;
	}

	public void setProcess_version(String process_version) {
		this.process_version = process_version;
	}

	public String getCurrent_version_flag() {
		return current_version_flag;
	}

	public void setCurrent_version_flag(String current_version_flag) {
		this.current_version_flag = current_version_flag;
	}

	public String getDefines() {
		return defines;
	}

	public void setDefines(String defines) {
		this.defines = defines;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ServiceModel getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(ServiceModel serviceModel) {
		this.serviceModel = serviceModel;
	}

}
