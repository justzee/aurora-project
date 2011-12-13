package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.ButtonFigure;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class ButtonPart extends ComponentPart {

	private String type;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {

		// l.setIcon(FlowImages.GEAR);
		return new ButtonFigure();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		ButtonFigure figure = (ButtonFigure) getFigure();
		Button model = (Button) getModel();
		figure.setLocation(model.getLocation());
		figure.setModel(model);

		IFigure parent = figure.getParent();
		// if (parent instanceof BoxFigure) {
		// figure.setLabelWidth(((BoxFigure) parent).getLabelWidth());
		// } else {
		// figure.setLabelWidth(ViewDiagram.DLabelWidth);
		// }
		super.refreshVisuals();

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

}
