package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

/**
 */
public class CreateComponentCommand extends Command {
	protected Container container;

	protected AuroraComponent child;
	private EditPart reference = null;

	public void setDiagram(Container container) {
		this.container = container;
	}

	public void setChild(AuroraComponent child) {
		this.child = child;
	}

	public void execute() {
		if (reference == null)
			container.addChild(child);
		else {
			AuroraComponent ac = (AuroraComponent) reference.getModel();
			List<AuroraComponent> list = container.getChildren();
			int idx = list.indexOf(ac);
			container.addChild(child, idx);
		}
	}

	public String getLabel() {
		return "Create Component";
	}

	public void redo() {
		this.execute();
	}

	public void undo() {
		container.removeChild(child);
	}

	public void setReferenceEditPart(EditPart reference) {
		this.reference = reference;
	}
}