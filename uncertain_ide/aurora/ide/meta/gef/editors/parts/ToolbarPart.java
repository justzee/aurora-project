package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.figures.ToolbarFigure;
import aurora.ide.meta.gef.editors.models.Button;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.DiagramLayoutEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public class ToolbarPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new ToolbarFigure();

		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (IProperties.CHILDREN.equals(prop)){
			Object newValue = evt.getNewValue();
			if(newValue instanceof Button){
				((Button) newValue).setButtonType(Button.ADD);
			}
		}
			
		super.propertyChange(evt);
	}

	protected void refreshVisuals() {
		super.refreshVisuals();

	}

	@Override
	protected void refreshChildren() {
		
		super.refreshChildren();
	}
	

}
