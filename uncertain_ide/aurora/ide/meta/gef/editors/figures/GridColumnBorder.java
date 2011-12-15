package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;

public class GridColumnBorder extends AbstractLabeledBorder {

//	private Insets padding = new Insets(1, 3, 2, 2);
	private String imageKey ;
	private GridColumnFigure figure;

	public GridColumnBorder(String title, String imageKey,
			GridColumnFigure figure) {
		super(title);
		this.imageKey = imageKey;
		this.figure = figure;
	}

	private Image getBGImage() {
		return ImagesUtils.getImage(imageKey);
	}

	@Override
	public Color getTextColor() {
		return ColorConstants.TITLETEXT;
	}

	public GridColumnBorder() {
	}

	public void paint(IFigure figure, Graphics g, Insets insets) {
		g.pushState();
		Rectangle rect = figure.getBounds();
		g.clipRect(rect);
		Rectangle r = rect.getResized(-1, -1);

		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);

		g.drawRectangle(r);

		Image i = getBGImage();
		Rectangle imageR = rect.getCopy().setHeight(getColumnHight());
		Rectangle src = new Rectangle(i.getBounds().x, i.getBounds().y,
				i.getBounds().width, 25);

		g.drawImage(i, src, imageR);

		Dimension textExtents = FigureUtilities.getTextExtents(getLabel(),
				getFont(figure));
		g.setFont(getFont(figure));
		g.setForegroundColor(getTextColor());
		g.drawString(getLabel(), imageR.getCenter().x - textExtents.width / 2,
				imageR.getCenter().y - textExtents.height / 2);

		g.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);

		g.drawRectangle(imageR.getResized(-1, -1));

		g.popState();
	}

	private int getColumnHight() {
		return figure.getColumnHight();
	}

	@Override
	protected Insets calculateInsets(IFigure figure) {
		return new Insets(0, 0, 0, 0);
	}
}
