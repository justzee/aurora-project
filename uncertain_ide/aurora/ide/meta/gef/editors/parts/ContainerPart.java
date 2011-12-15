package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.EditPolicy;

import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.IProperties;
import aurora.ide.meta.gef.editors.policies.DiagramLayoutEditPolicy;

public abstract class ContainerPart extends ComponentPart {

	protected List<?> getModelChildren() {
		return getContainer().getChildren();
	}

	private Container getContainer() {
		return (Container) getModel();
	}
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramLayoutEditPolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (IProperties.CHILDREN.equals(prop))
			refreshChildren();
	}
}
