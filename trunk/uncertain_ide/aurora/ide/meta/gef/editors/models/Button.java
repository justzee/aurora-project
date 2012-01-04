package aurora.ide.meta.gef.editors.models;

import java.util.Arrays;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
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
	private static final String[] std_types = { DEFAULT, ADD, SAVE, DELETE,
			CLEAR, EXCEL };
	private static final String[] std_type_names = { "", "新增", "保存", "删除",
			"清除", "导出" };
	public static final String BUTTON_TYPE = "buttontype";
	public static final String BUTTON_TEXT = "buttontext";
	public static final String BUTTON_FUNCTION = "buttonfunction";
	public static final String TOOLTIP = "tooltip";

	static final public String B_SEARCH = "b_search";
	static final public String B_RESET = "b_reset";
	static final public String B_SAVE = "b_save";
	static final public String B_CLOSE = "b_close";
	static final public String B_RUN = "b_run";
	static final public String B_OPEN = "b_open";

	private static final String[] inner_types = { DEFAULT, B_SEARCH, B_RESET,
			B_SAVE, B_OPEN, B_CLOSE, B_RUN };

	private static final String[] inner_types_names = { "自定义", "查询", "重置",
			"保存", "打开", "关闭", "运行" };
	private static final IPropertyDescriptor[] std_pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(BUTTON_TEXT, "Text"),
			new IntegerPropertyDescriptor(WIDTH, "Width"),
			new IntegerPropertyDescriptor(HEIGHT, "Height"),
			new StringPropertyDescriptor(TOOLTIP, "Tooltip"),
			new StringPropertyDescriptor(BUTTON_FUNCTION, "Click"),
			new ComboPropertyDescriptor(BUTTON_TYPE, "Type", std_type_names) };
	private static final IPropertyDescriptor[] inner_pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(BUTTON_TEXT, "Text"),
			new IntegerPropertyDescriptor(WIDTH, "Width"),
			new IntegerPropertyDescriptor(HEIGHT, "Height"),
			new StringPropertyDescriptor(TOOLTIP, "Tooltip"),
			new StringPropertyDescriptor(BUTTON_FUNCTION, "Click"),
			new ComboPropertyDescriptor(BUTTON_TYPE, "Type", inner_types_names) };

	private String buttonType = DEFAULT;
	private String text = "button";
	private String icon = "";
	private String function = "";
	private String title = "";
	private AuroraComponent targetComponent;

	private boolean isOnToolBar = false;

	public Button() {
		setSize(new Dimension(80, 20));
		this.setType("button");
	}

	@Override
	public void setSize(Dimension dim) {
		if (isOnToolBar) {
			dim.height = 20;
			if (isStdButton())
				dim.width = 48;
		}
		super.setSize(dim);
	}

	@Override
	public void setBounds(Rectangle bounds) {
		if (isOnToolBar) {
			bounds.height = 20;
			if (isStdButton())
				bounds.width = 48;
		}
		super.setBounds(bounds);
	}

	public boolean isStdButton() {
		return Arrays.asList(std_types).indexOf(buttonType) > 0;
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
		isOnToolBar = Toolbar.class.equals(targetComponent.getClass());
	}

	public void setButtonType(String buttonType) {
		if (eq(this.buttonType, buttonType))
			return;
		String oldV = this.buttonType;
		this.buttonType = buttonType;
		firePropertyChange(BUTTON_TYPE, oldV, buttonType);
		if (isStdButton()) {
			super.setSize(new Dimension(48, 20));
		}
	}

	public void setFunction(String function) {
		if (eq(this.function, function))
			return;
		String oldV = this.function;
		this.function = function;
		firePropertyChange(BUTTON_FUNCTION, oldV, function);
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
		firePropertyChange(BUTTON_TEXT, oldV, text);
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
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (isOnToolBar) {
			return std_pds;
		}
		return inner_pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (TOOLTIP.equals(propName))
			return getTitle();
		else if (BUTTON_TEXT.equals(propName))
			return getText();
		else if (BUTTON_FUNCTION.equals(propName))
			return getFunction();
		else if (BUTTON_TYPE.equals(propName))
			return Arrays.asList(isOnToolBar ? std_types : inner_types)
					.indexOf(getButtonType());
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (TOOLTIP.equals(propName))
			setTitle((String) val);
		else if (BUTTON_TEXT.equals(propName))
			setText((String) val);
		else if (BUTTON_FUNCTION.equals(propName))
			setFunction((String) val);
		else if (BUTTON_TYPE.equals(propName))
			setButtonType((isOnToolBar ? std_types : inner_types)[(Integer) val]);
		super.setPropertyValue(propName, val);
	}

}
