package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.NodeDirectEditPolicy;
import aurora.ide.meta.gef.editors.policies.NodeEditPolicy;

public abstract class ComponentPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, IProperties {

	public void propertyChange(PropertyChangeEvent evt) {
		this.getFigure().getBounds();
		String prop = evt.getPropertyName();
		if (!IProperties.CHILDREN.equals(prop))
			this.refreshVisuals();
	}

	@Override
	public void activate() {
		super.activate();
		getComponent().addPropertyChangeListener(this);
	}

	public AuroraComponent getComponent() {
		return (AuroraComponent) getModel();
	}

	@Override
	public void deactivate() {
		getComponent().removePropertyChangeListener(this);
		super.deactivate();
	}

	protected void refreshVisuals() {
		this.getFigure().setBounds(this.getComponent().getBounds());
		super.refreshVisuals();
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new NodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
	}
}
