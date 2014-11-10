package aurora.bpmn.designer.ws;

import java.util.ArrayList;
import java.util.List;

import aurora.bpmn.designer.rcp.viewer.INode;
import aurora.bpmn.designer.rcp.viewer.IParent;

public class BPMNDefineCategory implements IParent {

	private List<BPMNDefineCategory> categorys = new ArrayList<BPMNDefineCategory>();
	private List<BPMNDefineModel> defines = new ArrayList<BPMNDefineModel>();

	private IParent parent;

	private ServiceModel serviceModel;

	private String id;
	private String parent_id;
	private String name;

	public List<BPMNDefineCategory> getCategorys() {
		return categorys;
	}

	public void addCategory(BPMNDefineCategory category) {
		category.setParent(this);
		category.setServiceModel(serviceModel);
		this.categorys.add(category);
	}

	public List<BPMNDefineModel> getDefines() {
		return defines;
	}

	public void addDefine(BPMNDefineModel define) {
		define.setParent(this);
		define.setServiceModel(serviceModel);
		this.defines.add(define);
	}

	public IParent getParent() {
		return parent;
	}

	public void setParent(IParent parent) {
		this.parent = parent;
	}

	public ServiceModel getServiceModel() {
		return serviceModel;
	}

	public void setServiceModel(ServiceModel serviceModel) {
		this.serviceModel = serviceModel;
	}

	public void removeDefine(BPMNDefineModel define) {
		defines.remove(define);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	@Override
	public INode[] getChildren() {
		List<INode> nodes = new ArrayList<INode>();
		nodes.addAll(getCategorys());
		nodes.addAll(getDefines());
		return nodes.toArray(new INode[nodes.size()]);
	}

}
