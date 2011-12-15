package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class Container extends AuroraComponent {

	static final long serialVersionUID = 1;

	private static int count;

	protected List<AuroraComponent> children = new ArrayList<AuroraComponent>();

	public Container() {
		this.setSize(new Dimension(600, 80));
	}

	public void addChild(AuroraComponent child) {
		addChild(child, -1);
	}

	public void addChild(AuroraComponent child, int index) {
		if (!isResponsibleChild(child))
			return;
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

	public void removeChild(int idx) {
		AuroraComponent ac = children.remove(idx);
		fireStructureChange(CHILDREN, ac);
	}

	public boolean isResponsibleChild(AuroraComponent component) {
		return true;
	}

}
