package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.policies.DiagramLayoutEditPolicy;

public class BoxPart extends ContainerPart {


	@Override
	protected IFigure createFigure() {
		BoxFigure figure = new BoxFigure();
		BOX model = (BOX) getModel();
		figure.setBox(model);
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}

	protected void refreshVisuals() {
		BOX model = (BOX) getModel();
		BoxFigure figure = (BoxFigure) getFigure();
		Border border = figure.getBorder();
		if (border instanceof AbstractLabeledBorder) {
			((AbstractLabeledBorder) border).setLabel(model.getTitle());
		}
		super.refreshVisuals();

	}

}
