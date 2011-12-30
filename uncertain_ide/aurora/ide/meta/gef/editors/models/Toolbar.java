package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Toolbar extends RowCol {

	static final long serialVersionUID = 1;

	private List<Button> buttons;

	public Toolbar() {
		this.row = 1;
		this.col = 999;
		this.headHight = 2;
		this.setSize(new Dimension(1, 25));
	}

	public void addButton(Button b) {
		if (buttons == null) {
			buttons = new ArrayList<Button>();
		}
		buttons.add(b);
		this.addChild(b);
	}

	@SuppressWarnings("unchecked")
	public List<Button> getButtons() {
		return buttons == null ? Collections.EMPTY_LIST : buttons;
	}

	/**
	 * 
	 * 仅允许增加 Button
	 * */
	public boolean isResponsibleChild(AuroraComponent child) {
		return child instanceof Button;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return NONE_PROPS;
	}

}
