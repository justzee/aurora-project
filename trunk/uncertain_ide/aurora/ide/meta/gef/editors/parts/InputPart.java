package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.figures.InputField;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.ViewDiagram;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class InputPart extends ComponentPart {

	private String type;

	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
	
		// l.setIcon(FlowImages.GEAR);
		return new InputField();
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		type = this.getComponent().getType();
		this.getFigure().setLocation(
				((AuroraComponent) this.getModel()).getLocation());
		
		((InputField) getFigure()).setPrompt("prompt:");
		((InputField) getFigure()).setType(type);
		IFigure parent = ((InputField) getFigure()).getParent();
		if (parent instanceof BoxFigure) {
			((InputField) getFigure()).setLabelWidth(((BoxFigure) parent).getLabelWidth());
		} else {
			((InputField) getFigure()).setLabelWidth(ViewDiagram.DLabelWidth);
		}
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
