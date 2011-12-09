package aurora.ide.meta.gef.editors.models.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.Container;

/**
 */
public class CreateComponentCommand extends Command {
	protected Container container;

	protected AuroraComponent child;

	protected Point location;

	public void setDiagram(Container container) {
		this.container = container;
	}

	public void setChild(AuroraComponent child) {
		this.child = child;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public void execute() {
		if (this.location != null) {
			System.out.println(location);
			this.child.setLocation(this.location);
		}
	
		this.container.addChild(child);

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
}