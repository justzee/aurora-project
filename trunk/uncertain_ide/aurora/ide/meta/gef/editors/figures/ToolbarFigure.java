
package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

/**
 */
public class ToolbarFigure extends Figure {


	public ToolbarFigure() {
		this.setLayoutManager(new DummyLayout());
		this.setBorder(new TitleBorder(""));
	}


	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
	}

	public void paint(Graphics graphics) {
		if (getLocalBackgroundColor() != null)
			graphics.setBackgroundColor(getLocalBackgroundColor());
		if (getLocalForegroundColor() != null)
			graphics.setForegroundColor(getLocalForegroundColor());
		if (font != null)
			graphics.setFont(font);

		graphics.pushState();
		try {
			paintFigure(graphics);
			graphics.restoreState();
			paintBorder(graphics);
			paintClientArea(graphics);
			
		} finally {
			graphics.popState();
		}
	}
	
}
