package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

import aurora.ide.meta.gef.editors.ImagesUtils;

/**
 */
public class NavbarFigure extends Figure {

	private String[] texts = { "页数:", "共 页", "每页显示", "条", "显示 - 共 条" };

	public NavbarFigure() {
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

		Rectangle bounds = this.getBounds().getCopy();
		graphics.clipRect(bounds);

		Image image1 = getImage("nav1");
		Rectangle copy = bounds.getCopy();
		org.eclipse.swt.graphics.Rectangle imageBounds = image1.getBounds();

		Rectangle src = new Rectangle(imageBounds.x, imageBounds.y, 2, 25);
		graphics.drawImage(image1, src, copy);
		graphics.drawImage(image1, imageBounds.x, imageBounds.y,
				imageBounds.width, imageBounds.height, copy.x, copy.y,
				imageBounds.width, 25);

		Image image2 = getImage("nav2");

		Point topRight = copy.getTopRight();
		org.eclipse.swt.graphics.Rectangle imageBounds2 = image2.getBounds();
		int nav2X = topRight.x - imageBounds2.width;

		graphics.drawImage(image2, imageBounds2.x, imageBounds2.y,
				imageBounds2.width, imageBounds2.height, nav2X, topRight.y + 1,
				imageBounds2.width, 24);

		// int textIndex = 0;
		// Rectangle bounds = this.getBounds().getCopy();
		// graphics.clipRect(bounds);
		// Image image = getImage("navigation");
		// Rectangle copy = bounds.getCopy();
		// Rectangle imageR = copy.translate(2, 5);
		// // 首页
		// graphics.drawImage(image, getImageLocation(0).x,
		// getImageLocation(0).y,
		// 16, 16, imageR.x, imageR.y, 16, 16);
		// imageR.translate(2 + 16, 0);
		// // 前一页
		// graphics.drawImage(image, getImageLocation(0).x,
		// getImageLocation(2).y,
		// 16, 16, imageR.x, imageR.y, 16, 16);
		// imageR.translate(2 + 16, 0);
		// // 分割
		// Image sep = this.getImage("toolbar_sep");
		// org.eclipse.swt.graphics.Rectangle sepBounds = sep.getBounds();
		// graphics.drawImage(image, sepBounds.x, sepBounds.y, sepBounds.width,
		// sepBounds.height, imageR.x, imageR.y, sepBounds.width,
		// sepBounds.height);
		// imageR.translate(2 + sepBounds.width, 0);
		// // 页数
		// Dimension textExtents = FigureUtilities.getTextExtents(
		// texts[textIndex], getFont());
		// graphics.drawText(texts[textIndex], imageR.getLocation());
		// textIndex++;
		// imageR.translate(2 + textExtents.width, -1);
		//
		// // input
		// Rectangle inputR = imageR.getCopy().setSize(30, 20);
		// graphics.setForegroundColor(ColorConstants.BORDER);
		// graphics.setBackgroundColor(ColorConstants.WHITE);
		// graphics.fillRectangle(inputR.getResized(-2, -2));
		// graphics.drawRectangle(inputR.getResized(-2, -2));
		// imageR.translate(2 + 30, 1);
		// // "共 页",
		//
		// textExtents = FigureUtilities.getTextExtents(texts[textIndex],
		// getFont());
		// graphics.drawText(texts[textIndex], imageR.getLocation());
		// textIndex++;
		// imageR.translate(2 + textExtents.width, 0);
		//
		// // 分割
		//
		// graphics.drawImage(image, sepBounds.x, sepBounds.y, sepBounds.width,
		// sepBounds.height, imageR.x, imageR.y, sepBounds.width,
		// sepBounds.height);
		// imageR.translate(2 + sepBounds.width, 0);
		//
		// // 后一页
		// graphics.drawImage(image, getImageLocation(0).x,
		// getImageLocation(3).y,
		// 16, 16, imageR.x, imageR.y, 16, 16);
		// imageR.translate(2 + 16, 0);
		// // 最后一页
		// graphics.drawImage(image, getImageLocation(0).x,
		// getImageLocation(1).y,
		// 16, 16, imageR.x, imageR.y, 16, 16);
		// imageR.translate(2 + 16, 0);
		// // 刷新
		// graphics.drawImage(image, getImageLocation(0).x,
		// getImageLocation(4).y,
		// 16, 16, imageR.x, imageR.y, 16, 16);
		// imageR.translate(2 + 16, 0);
		// // "每页显示",,
		//
		// textExtents = FigureUtilities.getTextExtents(texts[textIndex],
		// getFont());
		// graphics.drawText(texts[textIndex], imageR.getLocation());
		// textIndex++;
		// imageR.translate(2 + textExtents.width, -1);
		//
		// // input
		// inputR = imageR.getCopy().setSize(50, 20);
		// graphics.setForegroundColor(ColorConstants.BORDER);
		// graphics.setBackgroundColor(ColorConstants.WHITE);
		// graphics.fillRectangle(inputR.getResized(-2, -2));
		// graphics.drawRectangle(inputR.getResized(-2, -2));
		// imageR.translate(2 + 50, 1);
		// // combo
		// image = getImage("itembar");
		//
		// if (image != null) {
		// graphics.drawImage(image, 0, 0, 16, 16,
		// inputR.getTopRight().x - 18, inputR.getTopRight().y, 16, 16);
		// }
		//
		// // "条"
		// textExtents = FigureUtilities.getTextExtents(texts[textIndex],
		// getFont());
		// graphics.drawText(texts[textIndex], imageR.getLocation());
		// textIndex++;
		// // imageR.translate(2 + textExtents.width, -1);
		// // "显示 - 共 条"
		// textExtents = FigureUtilities.getTextExtents(texts[textIndex],
		// getFont());
		// graphics.drawText(texts[textIndex], new Point(bounds.getTopRight().x
		// - textExtents.width, bounds.getTopRight().y - 1));
		// textIndex++;
		// // TODO 比较长度

	}

	private Image getImage(String key) {
		return ImagesUtils.getImage(key);
	}

}
