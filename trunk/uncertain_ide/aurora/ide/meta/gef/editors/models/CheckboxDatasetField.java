package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class CheckboxDatasetField extends DatasetField {
	/**
	 * 
	 */
	// check box
	// checkedValue="Y" defaultValue="Y"
	// lov
	// mapping = lov service:=
	private static final long serialVersionUID = -4619018857153616914L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(UNCHECKED_VALUE, "uncheckedValue"),
			new StringPropertyDescriptor(CHECKED_VALUE, "checkedValue") };

	private String checkedValue;
	private String uncheckedValue;

	public CheckboxDatasetField() {
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
		if (UNCHECKED_VALUE.equals(propName)) {
			return this.getUncheckedValue();
		}
		if (CHECKED_VALUE.equals(propName)) {
			return this.getCheckedValue();
		}
		return super.getPropertyValue(propName);
	}

	public String getCheckedValue() {
		return checkedValue;
	}

	public void setCheckedValue(String checkedValue) {
		this.checkedValue = checkedValue;
	}

	public String getUncheckedValue() {
		return uncheckedValue;
	}

	public void setUncheckedValue(String uncheckedValue) {
		this.uncheckedValue = uncheckedValue;
	}

}
