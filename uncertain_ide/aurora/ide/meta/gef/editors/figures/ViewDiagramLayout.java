package aurora.ide.meta.gef.editors.figures;

import java.util.List;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.layout.ScreenGraphLayout;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GraphAnimation;
import aurora.ide.meta.gef.editors.parts.ViewDiagramPart;

public class ViewDiagramLayout extends FlowLayout {

	private ComponentPart diagram;

	public ViewDiagramLayout(boolean b,ComponentPart diagram) {
		super(b);
		this.diagram = diagram;
	}

	@Override
	public void layout(IFigure parent) {
//		super.layout(parent);
		GraphAnimation.recordInitialState(parent);
		if (GraphAnimation.playbackState(parent))
			return;

		ScreenGraphLayout ly = new ScreenGraphLayout((ViewDiagramPart)diagram);
		ly.layout();
		parent.repaint();
//		diagram.getFigure().repaint();
		
//		CompoundDirectedGraph graph = new CompoundDirectedGraph();
//		graph.setDirection(PositionConstants.EAST);
//		Map partsToNodes = new HashMap();
//		Subgraph root = new Subgraph(diagram.getParent(),null);
//		diagram.contributeNodesToGraph(graph, root, partsToNodes);
////		new ScreenGraphLayout().visit(root);
//		new CompoundDirectedGraphLayout().visit(graph);
//		diagram.applyGraphResults(graph, partsToNodes);
////		super.layout(parent);
		
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

	protected void layoutRow(IFigure parent) {
		int majorAdjustment = 0;
		int minorAdjustment = 0;
		int correctMajorAlignment = getMajorAlignment();
		int correctMinorAlignment = getMinorAlignment();

		majorAdjustment = data.area.width - data.rowWidth + getMinorSpacing();

		switch (correctMajorAlignment) {
		case ALIGN_TOPLEFT:
			majorAdjustment = 10;
			break;
		case ALIGN_CENTER:
			majorAdjustment /= 2;
			break;
		case ALIGN_BOTTOMRIGHT:
			break;
		}

		for (int j = 0; j < data.rowCount; j++) {
			if (isStretchMinorAxis()) {
				data.bounds[j].height = data.rowHeight;
			} else {
				minorAdjustment = data.rowHeight - data.bounds[j].height;
				switch (correctMinorAlignment) {
				case ALIGN_TOPLEFT:
					minorAdjustment = 10;
					break;
				case ALIGN_CENTER:
					minorAdjustment /= 2;
					break;
				case ALIGN_BOTTOMRIGHT:
					break;
				}
				data.bounds[j].y += minorAdjustment;
			}
			data.bounds[j].x += majorAdjustment;

			setBoundsOfChild(parent, data.row[j], transposer.t(data.bounds[j]));
		}
		data.rowY += getMajorSpacing() + data.rowHeight;
		initRow();
	}

	
	
	
}
