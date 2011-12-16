package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;

public class CheckBox extends Input {

	private static final long serialVersionUID = 319077599101372088L;
	public static final String SELECTION_STATE = "selection_state";
	public static final String TEXT = "checkbox_text";
	private boolean selected = false;
	private String text = "text";

	public CheckBox() {
		setSize(new Dimension(120, 20));
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (this.selected == selected)
			return;
		boolean oldV = this.selected;
		this.selected = selected;
		firePropertyChange(SELECTION_STATE, oldV, selected);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (eq(this.text, text))
			return;
		String oldV = this.text;
		this.text = text;
		firePropertyChange(TEXT, oldV, text);
	}

}
