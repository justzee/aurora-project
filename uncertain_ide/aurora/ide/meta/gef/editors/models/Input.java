package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class Input extends AuroraComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1913613647491922330L;
	public static final String TEXT = "text";
	public static final String NUMBER = "number";
	public static final String Combo = "combo";
	public static final String LOV = "lov";
	public static final String CAL = "cal";
	private boolean required = false;
	private boolean readOnly = false;

	public Input() {
		this.setSize(new Dimension(120, 20));
		this.setType(TEXT);
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		if (this.required == required)
			return;
		boolean oldV = this.required;
		this.required = required;
		firePropertyChange(REQUIRED, oldV, required);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		if (this.readOnly == readOnly)
			return;
		boolean oldV = this.readOnly;
		this.readOnly = readOnly;
		firePropertyChange(READONLY, oldV, readOnly);
	}

	private static final IPropertyDescriptor[] pds = {
			new StringPropertyDescriptor(PROMPT, "Prompt"),
			new BooleanPropertyDescriptor(REQUIRED, "Required"),
			new BooleanPropertyDescriptor(READONLY, "Readonly") };

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	public Object getPropertyValue(Object propName) {
		if (REQUIRED.equals(propName))
			return this.isRequired();
		if (READONLY.equals(propName))
			return this.isReadOnly();
		return super.getPropertyValue(propName);
	}

	// private int indexOF(String s){
	// if(bbs[0].equals(s))
	// }

	public void setPropertyValue(Object propName, Object val) {
		if (REQUIRED.equals(propName))
			this.setRequired((Boolean) val);
		if (READONLY.equals(propName))
			this.setReadOnly((Boolean) val);
		super.setPropertyValue(propName, val);
	}

}
