package aurora.ide.meta.gef.editors.models;

import java.util.Arrays;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.BooleanPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
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
	public static final String CASE_LOWER = "lower";
	public static final String CASE_UPPER = "upper";
	public static final String CASE_ANY = "";
	private static final String[] CASE_TYPES = { CASE_ANY, CASE_UPPER,
			CASE_LOWER };
	public static final String ALLOWDECIMALS = "ALLOWDECIMALS";
	public static final String ALLOWNEGATIVE = "ALLOWNEGATIVE";
	public static final String ALLOWFORMAT = "ALLOWFORMAT";
	public static final String EMPTYTEXT = "EMPTYTEXT";
	public static final String TYPECASE = "TYPECASE";
	private boolean required = false;
	private boolean readOnly = false;
	private boolean allowDecimals = true;
	private boolean allowNegative = true;
	private boolean allowFormat = false;
	private String emptyText = "";
	private String typeCase = CASE_ANY;
	private static final IPropertyDescriptor PD_REQUIRED = new BooleanPropertyDescriptor(
			REQUIRED, "Required");
	private static final IPropertyDescriptor PD_READONLY = new BooleanPropertyDescriptor(
			READONLY, "ReadOnly");
	private static final IPropertyDescriptor PD_EMPYTEXT = new StringPropertyDescriptor(
			EMPTYTEXT, "EmptyText");
	private static final IPropertyDescriptor PD_TYPECASE = new ComboPropertyDescriptor(
			TYPECASE, "TypeCase", new String[] { "任意", "大写", "小写" });
	private static final IPropertyDescriptor[] pds_text = { PD_PROMPT,
			PD_REQUIRED, PD_READONLY, PD_WIDTH, PD_EMPYTEXT, PD_TYPECASE };
	private static final IPropertyDescriptor[] pds_number = { PD_PROMPT,
			PD_REQUIRED, PD_READONLY, PD_WIDTH, PD_EMPYTEXT,
			new BooleanPropertyDescriptor(ALLOWDECIMALS, "AllowDecimals"),
			new BooleanPropertyDescriptor(ALLOWNEGATIVE, "AllowNegative"),
			new BooleanPropertyDescriptor(ALLOWFORMAT, "AllowFormat") };

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

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (NUMBER.equals(getType()))
			return pds_number;
		return pds_text;
	}

	public Object getPropertyValue(Object propName) {
		if (REQUIRED.equals(propName))
			return this.isRequired();
		if (READONLY.equals(propName))
			return this.isReadOnly();
		else if (ALLOWDECIMALS.equals(propName))
			return isAllowDecimals();
		else if (ALLOWNEGATIVE.equals(propName))
			return isAllowNegative();
		else if (ALLOWFORMAT.equals(propName))
			return isAllowFormat();
		else if (EMPTYTEXT.equals(propName))
			return getEmptyText();
		else if (TYPECASE.equals(propName))
			return Arrays.asList(CASE_TYPES).indexOf(getTypeCase());
		return super.getPropertyValue(propName);
	}

	// private int indexOF(String s){
	// if(bbs[0].equals(s))
	// }

	public boolean isAllowDecimals() {
		return allowDecimals;
	}

	public void setAllowDecimals(boolean allowDecimals) {
		this.allowDecimals = allowDecimals;
	}

	public boolean isAllowNegative() {
		return allowNegative;
	}

	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public void setPropertyValue(Object propName, Object val) {
		if (REQUIRED.equals(propName))
			this.setRequired((Boolean) val);
		if (READONLY.equals(propName))
			this.setReadOnly((Boolean) val);
		else if (ALLOWDECIMALS.equals(propName))
			setAllowDecimals((Boolean) val);
		else if (ALLOWNEGATIVE.equals(propName))
			setAllowNegative((Boolean) val);
		else if (ALLOWFORMAT.equals(propName))
			setAllowFormat((Boolean) val);
		else if (EMPTYTEXT.equals(propName))
			setEmptyText((String) val);
		else if (TYPECASE.equals(propName))
			setTypeCase(CASE_TYPES[(Integer) val]);
		super.setPropertyValue(propName, val);
	}

	public boolean isAllowFormat() {
		return allowFormat;
	}

	public void setAllowFormat(boolean allowFormat) {
		this.allowFormat = allowFormat;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public void setEmptyText(String emptyText) {
		if (eq(this.emptyText, emptyText))
			return;
		String oldV = this.emptyText;
		this.emptyText = emptyText;
		firePropertyChange(EMPTYTEXT, oldV, emptyText);
	}

	public String getTypeCase() {
		return typeCase;
	}

	public void setTypeCase(String typeCase) {
		this.typeCase = typeCase;
	}

}
