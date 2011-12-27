package aurora.ide.meta.gef.editors.layout;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import aurora.ide.meta.gef.editors.parts.DatasetDiagramPart;

public class DatasetLayout extends AbstractLayout {
	private static final Insets PADDING = new Insets(2, 5, 2, 2);
	private Point lastLocation;
	private DatasetDiagramPart dsDiagram;

	public DatasetLayout(DatasetDiagramPart datasetDiagramPart) {
		dsDiagram = datasetDiagramPart;
	}

	public void layout(IFigure container) {
		lastLocation = newLine(0);
		boolean reLayout = false;
		List children = container.getChildren();
		// Rectangle bounds = container.getBounds();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			IFigure f = (IFigure) iterator.next();
			lastLocation.translate(PADDING.left, 0);
			Dimension size = f.getSize();
			if (lastLocation.x + size.width >= container.getSize().width) {
				lastLocation = newLine(f.getSize().height);
				lastLocation.translate(PADDING.left, 0);
				reLayout = true;
			}
			f.setLocation(lastLocation);
			lastLocation.translate(size.width, 0);
		}
		if (reLayout) {
//			IFigure fickRoot = container;
//			IFigure root = null;
//			while (fickRoot != null) {
//				
//				root = fickRoot;
//				System.out.println(root.getClass());
//				fickRoot = fickRoot.getParent();
//			}
//			System.out.println(root.getClass());
			Composite parent = dsDiagram.getViewer().getControl().getParent();
			GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
			layoutData.heightHint = 90;
			parent.setLayoutData(layoutData);
			parent.getParent().layout();
		}

	}

	private Point newLine(int y) {
		return new Point().translate(0, y + PADDING.top);
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		container.validate();
		List children = container.getChildren();
		Rectangle result = new Rectangle().setLocation(container
				.getClientArea().getLocation());
		for (int i = 0; i < children.size(); i++)
			result.union(((IFigure) children.get(i)).getBounds());
		result.resize(container.getInsets().getWidth(), container.getInsets()
				.getHeight());
		return result.getSize();
	}

}
