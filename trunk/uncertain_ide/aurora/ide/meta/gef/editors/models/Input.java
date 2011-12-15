package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;

public class Input extends AuroraComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1913613647491922330L;
	public static final String TEXT = "text";
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
}
