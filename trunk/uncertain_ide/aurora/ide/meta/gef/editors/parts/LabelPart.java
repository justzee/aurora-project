package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class LabelPart extends ComponentPart {

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		// l.setIcon(FlowImages.GEAR);
		InputField inputField = new InputField();

		return inputField;
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		Input model = (Input) getModel();
		InputField figure = (InputField) getFigure();
		figure.setLocation(model.getLocation());
		figure.setModel(model);
		IFigure parent = figure.getParent();
		if (parent instanceof BoxFigure) {
			figure.setLabelWidth(((BOX) ((BoxPart) parent).getComponent())
					.getLabelWidth());
		} else {
			figure.setLabelWidth(80);
		}
		super.refreshVisuals();

	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

}
