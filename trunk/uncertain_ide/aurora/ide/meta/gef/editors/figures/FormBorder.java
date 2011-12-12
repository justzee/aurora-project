package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;

public class FormBorder extends TitleBarBorder {
	private Insets padding = new Insets(1, 3, 2, 2);

	public FormBorder(String string) {
		super(string);
	}

	@Override
	public void paint(IFigure figure, Graphics g, Insets insets) {
		tempRect.setBounds(getPaintRectangle(figure, insets));

		FigureUtilities.paintEtchedBorder(g, tempRect);
		Rectangle rec = tempRect;
		rec.height = Math.min(rec.height, getTextExtents(figure).height
				+ padding.getHeight());
		g.clipRect(rec);

		g.fillRectangle(rec);
		Image i = ImagesUtils.getImage("toolbar_bg");
		Rectangle src = new Rectangle(i.getBounds().x, i.getBounds().y,
				i.getBounds().width, i.getBounds().height);
		g.drawImage(i, src, rec);

		int x = rec.x + padding.left;
		int y = rec.y + padding.top;

		int textWidth = getTextExtents(figure).width;
		int freeSpace = rec.width - padding.getWidth() - textWidth;

		if (getTextAlignment() == PositionConstants.CENTER)
			freeSpace /= 2;
		if (getTextAlignment() != PositionConstants.LEFT)
			x += freeSpace;

		g.setFont(getFont(figure));
		g.setForegroundColor(getTextColor());
		g.drawString(getLabel(), x, y);

		FigureUtilities.paintEtchedBorder(g, tempRect);

	}

	@Override
	public Color getTextColor() {
		return ColorConstants.TITLETEXT;
	}

}
