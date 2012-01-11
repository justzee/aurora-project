package aurora.ide.meta.gef.extension;

import aurora.ide.meta.gef.editors.wizard.ITemplateWizard;

public class ExtensionBean {
	private String id;
	private String name;
	private String description;
	private String thumbnail;
	private ITemplateWizard wizard;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public ITemplateWizard getWizard() {
		return wizard;
	}

	public void setWizard(ITemplateWizard wizard) {
		this.wizard = wizard;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject instanceof ExtensionBean) {
			return ((ExtensionBean) anObject).getId().equals(id) && ((ExtensionBean) anObject).getName().equals(name);
		}
		return false;
	}
}
