package aurora.ide.meta.gef.editors.layout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class _BoxBackLayout extends BackLayout {

	private int col;
	private int row;
	private int maxColHight;
	private int titleHight;
	private int totalRowNo;
	private Rectangle zero = new Rectangle(0, 0, 0, 0);
	private List<RowInfo> rowInfos;

	private class BoxData {
		int row;
		int col;
		Rectangle rect;
		ComponentPart part;
		ComponentPart left;
		ComponentPart right;
	}

	private List<BoxData> datas = new ArrayList<BoxData>();

	private class RowInfo {
		int rowNo;
		int maxColHight;
		int colSize;
		Rectangle temp = zero.getCopy();
		ComponentPart[] rowItem;
	}

	private Rectangle last = zero.getCopy();

	private int lastCol = 0;
	private int lastRow = 0;
	private Rectangle selfRectangle = new Rectangle();
	private BoxPart boxPart;

	private Point location = new Point();
	private Point templLocation;

	public Rectangle init(ComponentPart parent) {
		if (parent instanceof BoxPart) {
			BOX box = (BOX) parent.getComponent();
			col = box.getCol();
			row = box.getRow();
			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? box.getBounds() : fBounds;
			titleHight = box.getHeadHight();
			this.boxPart = (BoxPart) parent;
			int size = boxPart.getChildren().size();
			totalRowNo = size / col + size % col != 0 ? 1 : 0;
			rowInfos = new ArrayList<RowInfo>(totalRowNo);
			for (int r = 0; r < totalRowNo; r++) {
				RowInfo c_rowInfo = createRowInfo(r);
				rowInfos.add(c_rowInfo);
			}

		}
		return null;
	}

	public void ll() {
		List children = boxPart.getChildren();
		BoxData bd;
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			if (lastCol == col) {
				lastRow++;
				lastCol = 0;
			}
			bd = new BoxData();
			bd.col = lastCol;
			bd.row = lastRow;
			lastCol++;
			bd.part = ep;

			Rectangle layout = GraphLayoutManager.layout(ep);
			layout = newChildLocation(layout);
			// model--ep --figure;
			// parent.getFigure().getClientArea(Rectangle.SINGLETON);
			// layout.translate(Rectangle.SINGLETON.x, Rectangle.SINGLETON.y);

			// layout.translate(selfRectangle.getBottomLeft());
			applyToFigure(ep, layout);
		}
	}

	private void layoutRow(int r) {

	}

	private RowInfo createRowInfo(int rn) {
		RowInfo info = new RowInfo();
		info.rowNo = rn;
		List children = boxPart.getChildren();
		// children.

		return null;
	}

	public Rectangle layout(ComponentPart parent) {
		if (parent.getParent() instanceof BoxPart) {
			System.out.println();
		}

		if (parent instanceof BoxPart) {
			BOX box = (BOX) parent.getComponent();
			col = box.getCol();
			row = box.getRow();
			// selfRectangle = box.getBounds();

			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? box.getBounds() : fBounds;
			titleHight = box.getHeadHight();
			location.x = PADDING.left;
			location.y = titleHight + PADDING.top;
			location.translate(selfRectangle.getTopLeft());
			// selfRectangle.expand(PADDING);
			// templLocation = location.getCopy();
			// last.y += titleHight + PADDING.top;
			// last = last.translate(selfRectangle.getBottomLeft());
		}

		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			Rectangle layout = GraphLayoutManager.layout(ep);
			layout = llLocation(layout);
			// model--ep --figure;
			// parent.getFigure().getClientArea(Rectangle.SINGLETON);
			// layout.translate(Rectangle.SINGLETON.x, Rectangle.SINGLETON.y);
			// if(ep instanceof BoxPart){
			// System.out.println("变换布局之前。");
			// System.out.println(layout);
			// System.out.println(selfRectangle);
			// System.out.println(ep);
			// System.out.println("===========");
			// }
			// layout = layout.translate(selfRectangle.getTopLeft());
			// System.out.println("变换布局之后。");
			// System.out.println(layout);
			// System.out.println(selfRectangle);
			// System.out.println(ep);
			// System.out.println("===========");
			applyToFigure(ep, layout);
		}

		return calculateRectangle(parent);
	}

	private Rectangle calculateRectangle(ComponentPart parent) {
		Rectangle selfRectangle = zero.getCopy().setLocation(parent.getFigure().getBounds().getLocation());
		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart cp = (ComponentPart) children.get(i);
			 System.out.println("变换布局之前。");
			 System.out.println(selfRectangle);
			 System.out.println(cp.getFigure().getBounds());
			 System.out.println(cp);
			 System.out.println("===========");
			selfRectangle.union(cp.getFigure().getBounds().getCopy());
//			selfRectangle.union(cp.getFigure().getBounds().getSize());
			
			 System.out.println("变换布局之后。");
			 System.out.println(selfRectangle);
			 System.out.println(cp.getFigure().getBounds());
			 System.out.println(cp);
			 System.out.println("===========");
		}
		if (!selfRectangle.isEmpty()) {
//			return selfRectangle.expand(PADDING);
			return selfRectangle.expand(5, 5);
//			return selfRectangle;
		}
		selfRectangle = parent.getComponent().getBounds();		
		return selfRectangle;
	}

	private Rectangle llLocation(Rectangle layout) {
		if (lastCol == col) {
			lastRow++;
			lastCol = 0;
			// last = new Rectangle(zero.x, last.y + PADDING.top + maxColHight,
			// zero.width, zero.height);
			// location = this.templLocation.getCopy();
			location.x = PADDING.left+selfRectangle.getTopLeft().x;
			location.y = location.y + maxColHight + PADDING.top;
			maxColHight = 0;
			// location.translate(selfRectangle.getTopLeft());
			// last = last.translate(selfRectangle.getLocation());
		}
		// layout.x = location.x;
		// layout.y = location.y;
		layout.setLocation(location.getCopy());
		location.x += layout.width + PADDING.left;
		lastCol++;
		maxColHight = Math.max(maxColHight, layout.height);
		// last = layout.getCopy();
		// Transposer t = new Transposer();
		// t.t(last);

		// System.out.println(selfRectangle);
//		selfRectangle.union(layout.getCopy());
		// selfRectangle.expand(PADDING);

		// System.out.println(selfRectangle);
		return layout.getCopy();
	}

	public Rectangle lllll(ComponentPart parent) {
		if (parent instanceof BoxPart) {
			BOX box = (BOX) parent.getComponent();
			col = box.getCol();
			row = box.getRow();
			// selfRectangle = box.getBounds();

			Rectangle fBounds = parent.getFigure().getBounds();
			selfRectangle = fBounds.isEmpty() ? box.getBounds() : fBounds;
			titleHight = box.getHeadHight();
			last.y += titleHight + PADDING.top;
			// last = last.translate(selfRectangle.getLocation());
		}

		List children = parent.getChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			Rectangle layout = GraphLayoutManager.layout(ep);
			layout = newChildLocation(layout);
			// model--ep --figure;
			// parent.getFigure().getClientArea(Rectangle.SINGLETON);
			// layout.translate(Rectangle.SINGLETON.x, Rectangle.SINGLETON.y);

			layout = layout.translate(selfRectangle.getLocation());
			applyToFigure(ep, layout);
		}

		return selfRectangle;
	}

	@Override
	protected Rectangle newChildLocation(Rectangle layout) {
		if (lastCol == col) {
			lastRow++;
			lastCol = 0;
			last = new Rectangle(zero.x, last.y + PADDING.top + maxColHight,
					zero.width, zero.height);
			maxColHight = 0;
			// last = last.translate(selfRectangle.getLocation());
		}
		layout.x = last.x + last.width + PADDING.left;
		layout.y = last.y;
		lastCol++;
		maxColHight = Math.max(maxColHight, layout.height);
		last = layout.getCopy();
		// Transposer t = new Transposer();
		// t.t(last);

		// System.out.println(selfRectangle);
		selfRectangle.union(last.getCopy().translate(
				selfRectangle.getLocation()));
		// System.out.println(selfRectangle);
		return layout.getCopy();
	}

}
