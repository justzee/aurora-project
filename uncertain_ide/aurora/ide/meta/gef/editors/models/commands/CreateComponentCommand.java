package aurora.ide.meta.gef.editors.models.commands;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.Container;

import java.util.List;

import org.eclipse.gef.commands.Command;

/**
 */
public class CreateComponentCommand extends Command {
	protected Container container;

	protected AuroraComponent child;
	private AuroraComponent reference = null;

	public void setTargetContainer(Container container) {
		this.container = container;
	}

	public void setChild(AuroraComponent child) {
		this.child = child;
	}

	public void execute() {
		if (reference == null)
			container.addChild(child);
		else {
			List<AuroraComponent> list = container.getChildren();
			int idx = list.indexOf(reference);
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

	public void setReferenceModel(AuroraComponent reference) {
		this.reference = reference;
	}
}