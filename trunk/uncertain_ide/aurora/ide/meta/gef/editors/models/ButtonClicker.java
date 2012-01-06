package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.DialogEdiableObject;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class ButtonClicker extends AuroraComponent implements
		DialogEdiableObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1177281488586303137L;

	public static final String DEFAULT = "";

	static final public String B_SEARCH = "b_search";
	static final public String B_RESET = "b_reset";
	static final public String B_SAVE = "b_save";
	static final public String B_CLOSE = "b_close";
	static final public String B_RUN = "b_run";
	static final public String B_OPEN = "b_open";

	public static final String[] action_ids = { DEFAULT, B_SEARCH, B_RESET,
			B_SAVE, B_OPEN, B_CLOSE, B_RUN };

	public static final String[] action_texts = { "自定义", "查询", "重置", "保存",
			"打开", "关闭", "运行" };

	private String actionID;
	private String actionText = "查询";

	// b_open
	private String openPath;
	// b_close
	private String closeWindowID;
	// b_run
	private String runMessage;

	private Button button;

	// b_save,b_search,b_reset
	private AuroraComponent targetComponent;

	public ButtonClicker() {

	}

	@Override
	public void setSize(Dimension dim) {

	}

	@Override
	public void setBounds(Rectangle bounds) {
	}

	public AuroraComponent getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(AuroraComponent targetComponent) {
		this.targetComponent = targetComponent;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return null;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		return null;
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
	}

	public String getActionID() {
		return actionID;
	}

	public void setActionID(String actionID) {
		this.actionID = actionID;
	}

	public String getActionText() {
		return actionText;
	}

	public void setActionText(String actionText) {
		this.actionText = actionText;
	}

	public String getOpenPath() {
		return openPath;
	}

	public void setOpenPath(String openPath) {
		this.openPath = openPath;
	}

	public String getCloseWindowID() {
		return closeWindowID;
	}

	public void setCloseWindowID(String closeWindowID) {
		this.closeWindowID = closeWindowID;
	}

	public String getRunMessage() {
		return runMessage;
	}

	public void setRunMessage(String runMessage) {
		this.runMessage = runMessage;
	}

	public String getDescripition() {
		return getActionText();
	}

	public Object getContextInfo() {
		return button;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

}
