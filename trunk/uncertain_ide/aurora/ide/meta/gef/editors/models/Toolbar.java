package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class Toolbar extends Container {

	static final long serialVersionUID = 1;

	private static int count;

	protected List<AuroraComponent> children = new ArrayList<AuroraComponent>();

	public Toolbar() {
		this.setSize(new Dimension(600, 80));
	}

	public void addChild(AuroraComponent child) {
		addChild(child, -1);
	}

	public void addChild(AuroraComponent child, int index) {
		if (index >= 0)
			children.add(index, child);
		else
			children.add(child);
		fireStructureChange(CHILDREN, child);
	}

	public List<AuroraComponent> getChildren() {
		return children;
	}

	public String getNewID() {
		return Integer.toString(count++);
	}

	public void removeChild(AuroraComponent child) {
		children.remove(child);
		fireStructureChange(CHILDREN, child);
	}

}
