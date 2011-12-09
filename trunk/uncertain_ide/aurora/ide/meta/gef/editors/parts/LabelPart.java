package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
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

		this.getFigure().setLocation(
				((AuroraComponent) this.getModel()).getLocation());
		((InputField) getFigure()).setPrompt("prompt:");
		IFigure parent = ((InputField) getFigure()).getParent();
		if (parent instanceof BoxFigure) {
			((InputField) getFigure()).setLabelWidth(((BOX) ((BoxPart) parent).getComponent())
					.getLabelWidth());
		} else {
			((InputField) getFigure()).setLabelWidth(80);
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
