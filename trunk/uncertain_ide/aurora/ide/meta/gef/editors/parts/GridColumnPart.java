package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.GridColumnFigure;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.policies.FormLayoutEditPolicy;

public class GridColumnPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new GridColumnFigure();

		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FormLayoutEditPolicy());
		//TODO resize Policys
//		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, policy);
	}

	protected void refreshVisuals() {
		((GridColumnFigure) getFigure()).setModel((GridColumn) this
				.getComponent());
		super.refreshVisuals();
	}

}
