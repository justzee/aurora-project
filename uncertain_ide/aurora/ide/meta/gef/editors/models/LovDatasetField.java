package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class LovDatasetField extends DatasetField {
	/**
	 * 
	 */
	// check box
	// checkedValue="Y" defaultValue="Y"
	// lov
	// mapping = lov service:=
	private static final long serialVersionUID = -4619018857153616914L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(LOV_GRID_HEIGHT, "lovGridHeight"),
			new StringPropertyDescriptor(LOV_HEIGHT, "lovHeight"),
			new StringPropertyDescriptor(LOV_SERVICE, "lovService"),
			new StringPropertyDescriptor(LOV_URL, "lovUrl"),
			new StringPropertyDescriptor(LOV_WIDTH, "lovWidth"),
			new StringPropertyDescriptor(TITLE, "title") };

	private String lovGridHeight;
	private String lovHeight;
	private String lovService;
	private String lovUrl;
	private String lovWidth;
	private String title;

	public LovDatasetField() {
		this.setType("field");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] propertyDescriptors = super
				.getPropertyDescriptors();
		return this.mergePropertyDescriptor(propertyDescriptors, pds);
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (LOV_GRID_HEIGHT.equals(propName)) {
			return this.getLovGridHeight();
		}
		if (LOV_HEIGHT.equals(propName)) {
			return this.getLovHeight();
		}
		if (LOV_SERVICE.equals(propName)) {
			return this.getLovService();
		}
		if (LOV_URL.equals(propName)) {
			return this.getLovUrl();
		}
		if (LOV_WIDTH.equals(propName)) {
			return this.getLovWidth();
		}
		if (TITLE.equals(propName)) {
			return this.getTitle();
		}

		return super.getPropertyValue(propName);
	}

	public String getLovGridHeight() {
		return lovGridHeight;
	}

	public void setLovGridHeight(String lovGridHeight) {
		this.lovGridHeight = lovGridHeight;
	}

	public String getLovHeight() {
		return lovHeight;
	}

	public void setLovHeight(String lovHeight) {
		this.lovHeight = lovHeight;
	}

	public String getLovService() {
		return lovService;
	}

	public void setLovService(String lovService) {
		this.lovService = lovService;
	}

	public String getLovUrl() {
		return lovUrl;
	}

	public void setLovUrl(String lovUrl) {
		this.lovUrl = lovUrl;
	}

	public String getLovWidth() {
		return lovWidth;
	}

	public void setLovWidth(String lovWidth) {
		this.lovWidth = lovWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
