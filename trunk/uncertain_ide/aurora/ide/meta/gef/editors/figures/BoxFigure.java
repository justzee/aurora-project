package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;

import aurora.ide.meta.gef.editors.models.BOX;

/**

 */
public class BoxFigure extends Figure {

	private int labelWidth;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		if (BOX.HBOX.equals(type)) {
			this.setBorder(new VirtualBoxBorder("H"));
		}
		if (BOX.VBOX.equals(type)) {
			this.setBorder(new VirtualBoxBorder("V"));
		}
	}

	public BoxFigure() {
		this.setLayoutManager(new DummyLayout());
		this.setBorder(new TitleBorder("大家好 ： 敬请期待。。。"));
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
	}

}
