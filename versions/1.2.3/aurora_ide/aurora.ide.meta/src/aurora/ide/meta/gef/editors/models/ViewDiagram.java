package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import aurora.ide.meta.gef.editors.template.Template;

public class ViewDiagram extends Container {
	private static final long serialVersionUID = -9196440587781890208L;
	public static final int DLabelWidth = 80;
	private static Class<?>[] unsupported = { Toolbar.class, Navbar.class, GridColumn.class, TabItem.class };

	private List<InitModel> initModels = new ArrayList<InitModel>();
	private String bindTemplate = "";

	private String templateType;

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		Class<?> cls = component.getClass();
		for (Class<?> c : unsupported)
			if (c.equals(cls))
				return false;
		return super.isResponsibleChild(component);
	}

	public List<InitModel> getInitModels() {
		return initModels;
	}

	public void addInitModels(InitModel initModel) {
		this.initModels.add(initModel);
	}

	public String getBindTemplate() {
		return bindTemplate;
	}

	public void setBindTemplate(String bindTemplate) {
		this.bindTemplate = bindTemplate;
	}

	public boolean isBindTemplate() {
		return bindTemplate != null && !"".equals(bindTemplate.trim());
	}

	public List<String> getModels() {
		List<String> models = new ArrayList<String>();
		List<Container> containers = getContainers(this);
		for (Container container : containers) {
			String sectionType = container.getSectionType();
			if (Container.SECTION_TYPE_QUERY.equals(sectionType) || Container.SECTION_TYPE_RESULT.equals(sectionType)) {
				String model = container.getDataset().getModel();
				if (null != model) {
					models.add(model);
				}
			}
		}
		return models;
	}

	public List<Container> getSectionContainers(Container container, String[] types) {
		List<Container> containers = new ArrayList<Container>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				String sectionType = ((Container) ac).getSectionType();
				if (contains(types, sectionType))
					containers.add((Container) ac);
				containers.addAll(getSectionContainers((Container) ac, types));
			}
//			if(ac instanceof TabItem){
//				TabBody body = ((TabItem) ac).getBody();
//				containers.addAll(getSectionContainers(body, types));
//			}
		}
		return containers;
	}

	private boolean contains(Object[] types, Object type) {
		for (Object string : types) {
			if (string.equals(type)) {
				return true;
			}
		}
		return false;
	}

	public List<Container> getContainers(Container container) {
		List<Container> containers = new ArrayList<Container>();
		List<AuroraComponent> children = container.getChildren();
		for (AuroraComponent ac : children) {
			if (ac instanceof Container) {
				containers.add((Container) ac);
				containers.addAll(getContainers((Container) ac));
			}
//			if(ac instanceof TabItem){
//				TabBody body = ((TabItem) ac).getBody();
//				containers.addAll(getContainers(body));
//			}
		}
		return containers;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public boolean isForDisplay() {
		return Template.TYPE_DISPLAY.equals(templateType);
	}

	public boolean isForCreate() {
		return Template.TYPE_CREATE.equals(templateType);
	}

	public boolean isForUpdate() {
		return Template.TYPE_UPDATE.equals(templateType);
	}

	public boolean isForSearch() {
		return Template.TYPE_SERACH.equals(templateType);
	}
}