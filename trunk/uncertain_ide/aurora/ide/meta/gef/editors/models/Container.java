package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class Container extends AuroraComponent {

	static final long serialVersionUID = 1;

	private static int count;

	private Dataset dataset;

	protected List<AuroraComponent> children = new ArrayList<AuroraComponent>();

	public Container() {
		this.setSize(new Dimension(600, 80));
	}

	public void addChild(AuroraComponent child) {
		addChild(child, children.size());
	}

	public void addChild(AuroraComponent child, int index) {
		if (!isResponsibleChild(child))
			return;
		children.add(index, child);
		child.setParent(this);
		fireStructureChange(CHILDREN, child);
	}

	public List<AuroraComponent> getChildren() {
		return children;
	}

	public String getNewID() {
		return Integer.toString(count++);
	}

	public void removeChild(AuroraComponent child) {
//		child.setParent(null);
		children.remove(child);
		fireStructureChange(CHILDREN, child);
	}

	public void removeChild(int idx) {
//		children.get(idx).setParent(null);
		AuroraComponent ac = children.remove(idx);
		fireStructureChange(CHILDREN, ac);
	}

	public boolean isResponsibleChild(AuroraComponent component) {
		return true;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public AuroraComponent getFirstChild(Class clazz) {
		List<AuroraComponent> children = this.getChildren();
		for (Iterator iterator = children.iterator(); iterator.hasNext();) {
			AuroraComponent auroraComponent = (AuroraComponent) iterator.next();
			if (auroraComponent.getClass().equals(clazz))
				return (AuroraComponent) auroraComponent;
		}
		return null;
	}

}
