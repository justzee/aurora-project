package aurora.ide.meta.gef.editors.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;

public class ScreenGraphLayout extends BackLayout {
	// private Point last = new Point(PADDING.left, PADDING.top);
	private ViewDiagramPart diagram;

	private Rectangle last = new Rectangle(0, 0, 0, 0);
	private Rectangle zero = new Rectangle(0, 0, 0, 0);

	// public Subgraph layout(Subgraph graph) {
	//
	// NodeList members = graph.members;
	// Insets insets = graph.insets;
	// insets.top;
	// insets.left;
	// insets.bottom;
	// insets.right;
	// for (int i = 0; i < members.size(); i++) {
	// Node node = members.getNode(i);
	// node.width;
	// node.height;
	// node.x;
	// node.y;
	//
	// if(node instanceof Subgraph){
	// node = layout((Subgraph)node);
	// }
	// }
	// return graph;
	// }
	public ScreenGraphLayout(ViewDiagramPart diagram) {
		this.diagram = diagram;
	}

	public void layout() {
		List children = getSortChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart ep = (ComponentPart) children.get(i);
			Rectangle layout = GraphLayoutManager.layout(ep);
			layout = newChildLocation(ep, layout);
			applyToFigure(ep, layout);
		}
	}

	private List getSortChildren() {
		List children = diagram.getChildren();
		// List sortChildren = new ArrayList(children);
		//
		// Collections.sort(sortChildren, new Comparator() {
		//
		// public int compare(Object o1, Object o2) {
		// ComponentPart ep1 = (ComponentPart) o1;
		// ComponentPart ep2 = (ComponentPart) o2;
		// Rectangle bound1 = ep1.getFigure().getBounds();
		// Rectangle bound2 = ep2.getFigure().getBounds();
		// Rectangle bounds1M = ep1.getComponent().getBounds();
		// Rectangle bounds2M = ep2.getComponent().getBounds();
		// Rectangle epl1 = bound1.isEmpty() ? bounds1M : bound1;
		// Rectangle epl2 = bound2.isEmpty() ? bounds2M : bound2;
		// return epl1.y - epl2.y;
		// }
		//
		// });
		return children;
	}

	public Rectangle layout(ComponentPart ep) {
		return null;
	}

	protected Rectangle newChildLocation(ComponentPart ep, Rectangle layout) {
		// System.out.println(layout);
		// this.getPreviousPage(page)
		// Rectangle last = new Rectangle(0, 0, 0, 0);
		// ComponentPart pPart = findPreviouseChild(ep,layout);

		// if (pPart != null)
		// last = pPart.getFigure().getBounds();
		layout.x = PADDING.left;
		layout.y = last.y + last.height + PADDING.top;
		last = layout.getCopy();
		return layout.getCopy();
	}

	private ComponentPart findPreviouseChild(ComponentPart ep, Rectangle layout) {
		List children = getSortChildren();
		for (int i = 0; i < children.size(); i++) {
			ComponentPart child = (ComponentPart) children.get(i);
			Rectangle bounds = child.getFigure().getBounds();
			if (layout.y <= bounds.y + bounds.height + PADDING.top
					&& (!child.equals(ep))) {
				return child;
			}
			if (i == children.size() - 1 && (!child.equals(ep))) {
				return child;
			}
		}

		return null;

	}

}
