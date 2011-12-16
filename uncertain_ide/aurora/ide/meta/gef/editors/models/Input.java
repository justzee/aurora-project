package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

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

	private static final String[] bbs = new String[] { "true", "false" };
	private static final IPropertyDescriptor[] pds = {
			new TextPropertyDescriptor(PROMPT, "Prompt"),
			new ComboBoxPropertyDescriptor(REQUIRED, "Required", bbs),
			new ComboBoxPropertyDescriptor(READONLY, "Readonly", bbs) };

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	public Object getPropertyValue(Object propName) {
		if (REQUIRED.equals(propName))
			return this.isRequired() ? 0 : 1;
		if (READONLY.equals(propName))
			return this.isReadOnly() ? 0 : 1;
		return super.getPropertyValue(propName);
	}

	// private int indexOF(String s){
	// if(bbs[0].equals(s))
	// }

	public void setPropertyValue(Object propName, Object val) {
		if (REQUIRED.equals(propName))
			this.setRequired(Boolean.valueOf(bbs[Integer.valueOf(val.toString())]));
		if (READONLY.equals(propName))
			this.setReadOnly(Boolean.valueOf(bbs[Integer.valueOf(val.toString())]));
		super.setPropertyValue(propName, val);
	}

}
