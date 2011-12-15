package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;

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

	private String buttonType = DEFAULT;
	private String text = "button";
	private String icon = "";
	private String function;
	private String title;

	public Button() {
		setSize(new Dimension(80, 20));
	}

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

	protected boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
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

	public void setButtonType(String buttonType) {
		if (eq(this.buttonType, buttonType))
			return;
		String oldV = this.buttonType;
		this.buttonType = buttonType;
		firePropertyChange("BUTTONTYPE", oldV, buttonType);
		if (isStdButton()) {
			super.setSize(new Dimension(48, 20));
		}
	}

	public void setFunction(String function) {
		if (eq(this.function, function))
			return;
		String oldV = this.function;
		this.function = function;
		firePropertyChange("FUNCTION", oldV, function);
	}

	public void setIcon(String icon) {
		if (eq(this.icon, icon))
			return;
		String oldV = this.icon;
		this.icon = icon;
		firePropertyChange("ICON", oldV, icon);
	}

	public void setText(String text) {
		if (this.text.equals(text))
			return;
		String oldV = this.text;
		this.text = text;
		firePropertyChange("TEXT", oldV, text);
	}

	public void setTitle(String title) {
		if (eq(this.title, title))
			return;
		String oldV = this.title;
		this.title = title;
		firePropertyChange("TITLE", oldV, title);
	}

}
