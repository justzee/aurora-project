package aurora.ide.meta.gef.editors.models;

import java.util.Arrays;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class Button extends AuroraComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1177281488586303137L;
	public static final String ADD = "add";
	public static final String SAVE = "save";
	public static final String DELETE = "delete";
	public static final String CLEAR = "clear";
	public static final String EXCEL = "excel";
	public static final String DEFAULT = "default";
	private static final String[] types = new String[] { DEFAULT, ADD, SAVE,
			DELETE, CLEAR, EXCEL };
	public static final String BUTTONTYPE = "buttontype";
	public static final String BUTTONTEXT = "buttontext";
	public static final String BUTTONFUNCTION = "buttonfunction";
	public static final String TOOLTIP = "tooltip";

	private String buttonType = DEFAULT;
	private String text = "button";
	private String icon = "";
	private String function = "";
	private String title = "";
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(BUTTONTEXT, "Text"),
			new IntegerPropertyDescriptor(WIDTH, "Width"),
			new IntegerPropertyDescriptor(HEIGHT, "Height"),
			new StringPropertyDescriptor(TOOLTIP, "Tooltip"),
			new StringPropertyDescriptor(BUTTONFUNCTION, "Click"),
			new ComboPropertyDescriptor(BUTTONTYPE, "Type", types) };

	private AuroraComponent targetComponent;

	public Button() {
		setSize(new Dimension(80, 20));
	}

	static final public String B_SEARCH = "b_search";
	static final public String B_RESET = "b_reset";
	static final public String B_SAVE = "b_save";
	static final public String B_CLOSE = "b_close";
	static final public String B_RUN = "b_run";
	static final public String B_OPEN = "b_open";

	public void setSize(Dimension dim) {
		if (isStdButton()) {
			dim.height = 20;
			dim.width = 48;
		}
		super.setSize(dim);
	}

	public boolean isStdButton() {
		return !buttonType.equals(DEFAULT);
	}

	public String getButtonType() {
		return buttonType;
	}

	public String getFunction() {
		return function;
	}

	public String getIcon() {
		return icon;
	}

	public String getText() {
		if (CLEAR.equals(buttonType))
			return "清除";
		else if (ADD.equals(buttonType))
			return "新增";
		else if (DELETE.equals(buttonType))
			return "删除";
		else if (SAVE.equals(buttonType))
			return "保存";
		else if (EXCEL.equals(buttonType))
			return "导出";
		return text;
	}

	public String getTitle() {
		return title;
	}

	public AuroraComponent getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(AuroraComponent targetComponent) {
		this.targetComponent = targetComponent;
	}

	public void setButtonType(String buttonType) {
		if (eq(this.buttonType, buttonType))
			return;
		String oldV = this.buttonType;
		this.buttonType = buttonType;
		firePropertyChange(BUTTONTYPE, oldV, buttonType);
		if (isStdButton()) {
			super.setSize(new Dimension(48, 20));
		}
	}

	public void setFunction(String function) {
		if (eq(this.function, function))
			return;
		String oldV = this.function;
		this.function = function;
		firePropertyChange(BUTTONFUNCTION, oldV, function);
	}

	public void setIcon(String icon) {
		if (eq(this.icon, icon))
			return;
		String oldV = this.icon;
		this.icon = icon;
		firePropertyChange("ICON", oldV, icon);
	}

	public void setText(String text) {
		if (eq(this.text, text))
			return;
		String oldV = this.text;
		this.text = text;
		firePropertyChange(BUTTONTEXT, oldV, text);
	}

	public void setTitle(String title) {
		if (eq(this.title, title))
			return;
		String oldV = this.title;
		this.title = title;
		firePropertyChange(TOOLTIP, oldV, title);
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (TOOLTIP.equals(propName))
			return getTitle();
		else if (BUTTONTEXT.equals(propName))
			return getText();
		else if (BUTTONFUNCTION.equals(propName))
			return getFunction();
		else if (BUTTONTYPE.equals(propName))
			return Arrays.asList(types).indexOf(getButtonType());
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (TOOLTIP.equals(propName))
			setTitle((String) val);
		else if (BUTTONTEXT.equals(propName))
			setText((String) val);
		else if (BUTTONFUNCTION.equals(propName))
			setFunction((String) val);
		else if (BUTTONTYPE.equals(propName))
			setButtonType(types[(Integer) val]);
		super.setPropertyValue(propName, val);
	}

}
