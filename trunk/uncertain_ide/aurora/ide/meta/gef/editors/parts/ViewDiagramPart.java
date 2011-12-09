package aurora.ide.meta.gef.editors.parts;

import java.util.EventObject;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.CommandStackListener;

import aurora.ide.meta.gef.editors.figures.ViewDiagramLayout;
import aurora.ide.meta.gef.editors.policies.DiagramLayoutEditPolicy;

public class ViewDiagramPart extends ContainerPart {

	CommandStackListener stackListener = new CommandStackListener() {
		public void commandStackChanged(EventObject event) {
			if (!GraphAnimation.captureLayout(getFigure()))
				return;
			while (GraphAnimation.step())
				getFigure().getUpdateManager().performUpdate();
			GraphAnimation.end();
		}
	};

	@Override
	protected IFigure createFigure() {
		Figure figure = new FreeformLayer();
		ViewDiagramLayout manager = new ViewDiagramLayout(false, this);

		// manager.setStretchMinorAxis(false);

		manager.setMajorSpacing(80);
		manager.setMinorSpacing(50);

		figure.setLayoutManager(manager);

		// figure.setSize(1000, 1000);
		return figure;
	}

	public void applyGraphResults(CompoundDirectedGraph graph, Map map) {
		Dimension size2 = this.getFigure().getSize();
		for (int i = 0; i < getChildren().size(); i++) {
			ComponentPart part = (ComponentPart) getChildren().get(i);
			part.applyGraphResults(graph, map);
		}
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
		super.activate();
		getViewer().getEditDomain().getCommandStack()
				.addCommandStackListener(stackListener);
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		getViewer().getEditDomain().getCommandStack()
				.removeCommandStackListener(stackListener);
		super.deactivate();
	}

	@Override
	protected void addChild(EditPart child, int index) {
//		Rectangle layout = ((ComponentPart) child).getComponent().getBounds();
//		ComponentPart previouseChild = this.findPreviouseChild(layout);
//		if (previouseChild != null) {
//			index = getChildren().indexOf(previouseChild) +1;
//		}
		super.addChild(child, index);
	}


}
