package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.policies.FormLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class BoxPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new BoxFigure();

		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FormLayoutEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

	protected void refreshVisuals() {
		((BoxFigure) getFigure()).setLabelWidth(((BOX) this.getModel())
				.getLabelWidth());
		((BoxFigure) getFigure()).setType(((BOX) this.getModel()).getType());
		super.refreshVisuals();

	}

}
