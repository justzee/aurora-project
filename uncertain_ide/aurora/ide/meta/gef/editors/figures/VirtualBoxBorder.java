package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;

public class VirtualBoxBorder extends AbstractLabeledBorder {
	String text = null;
	static Image img = ImagesUtils.getImage("element.gif");

	public VirtualBoxBorder(String text) {
		this.text = text;
	}

	public void paint(IFigure figure, Graphics g, Insets insets) {
		g.pushState();
		Rectangle rect = figure.getBounds();
		g.clipRect(rect);
		Rectangle r = rect.getResized(-1, -1);
		g.setForegroundColor(ColorConstants.VBORDER);
		g.setLineStyle(Graphics.LINE_DOT);
		g.drawRectangle(r);
		if (img != null) {
			g.drawImage(img, r.x, r.y);
		} else {
			g.drawText(text, r.x, r.y);
		}
		g.popState();
	}

	@Override
	protected Insets calculateInsets(IFigure figure) {
		return new Insets(0, 0, 0, 0);
	}
}
