package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;

public class Button extends AuroraComponent {
	public static final String SAVE = "save";
	public static final String DELETE = "delete";
	public static final String CLEAR = "clear";
	public static final String ADD = "add";
	public static final String DEFAULT = "default";

	private String buttonType = DEFAULT;
	private String text = "button";
	private String icon;
	private String function;
	private String title;

	public Button() {
		setSize(new Dimension(80, 20));
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

	// 在 figure 中使用
	public Image getImage() {
		if (CLEAR.equals(buttonType))
			return ImagesUtils.getImage("aurora/clear.gif");
		else if (ADD.equals(buttonType))
			return ImagesUtils.getImage("aurora/add.gif");
		else if (DELETE.equals(buttonType))
			return ImagesUtils.getImage("aurora/delete.gif");
		else if (SAVE.equals(buttonType))
			return ImagesUtils.getImage("aurora/save.gif");
		return ImagesUtils.getImage("aurora/default.gif");
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
		return text;
	}

	public String getTitle() {
		return title;
	}

	public void setButtonType(String buttonType) {
		if (this.buttonType.equals(buttonType))
			return;
		String oldV = this.buttonType;
		this.buttonType = buttonType;
		firePropertyChange("BUTTONTYPE", oldV, buttonType);
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
