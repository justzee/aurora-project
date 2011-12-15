package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class Toolbar extends Container {

	static final long serialVersionUID = 1;

	private List<Button> buttons;

	public Toolbar() {
		this.setSize(new Dimension(0, 25));
	}

	public void addButton(Button b) {
		if (buttons == null) {
			buttons = new ArrayList<Button>();
		}
		buttons.add(b);
		this.addChild(b);
	}

	/**
	 * 
	 * 仅允许增加 Button
	 * */
	@Override
	public void addChild(AuroraComponent child, int index) {
		if (child instanceof Button)
			super.addChild(child, index);
	}

	@SuppressWarnings("unchecked")
	public List<Button> getButtons() {
		return buttons == null ? Collections.EMPTY_LIST : buttons;
	}
}
