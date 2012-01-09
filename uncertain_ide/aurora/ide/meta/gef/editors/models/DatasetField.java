package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class DatasetField extends AuroraComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4619018857153616914L;

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new BooleanPropertyDescriptor(REQUIRED, "Required"),
			new BooleanPropertyDescriptor(READONLY, "ReadOnly"),
			new StringPropertyDescriptor(NAME, "Name") };

	private boolean required = false;
	private boolean readOnly = false;

	public DatasetField() {
		this.setType("field");
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (NAME.equals(propName)) {
			return this.getName();
		}
		if (READONLY.equals(propName)) {
			return this.isReadOnly();
		}
		if (REQUIRED.equals(propName)) {
			return this.isRequired();
		}
		return null;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
