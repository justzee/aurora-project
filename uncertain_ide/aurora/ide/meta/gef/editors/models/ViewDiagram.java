package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

public class ViewDiagram extends Container {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9196440587781890208L;
	public static final int DLabelWidth = 80;
	private static Class[] unsupported = { Toolbar.class, Navbar.class,
			GridColumn.class, TabItem.class };

	private List<Dataset> datasets = new ArrayList<Dataset>();

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		// if (component instanceof Grid)
		// return true;
		// if (component instanceof Toolbar || component instanceof Navbar
		// || component instanceof GridColumn)
		// return false;
		Class cls = component.getClass();
		for (Class c : unsupported)
			if (c.equals(cls))
				return false;
		return super.isResponsibleChild(component);
	}
	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public void addDataset(Dataset ds) {
		datasets.add(ds);
	}
	public void removeDataset(Dataset ds) {
		datasets.remove(ds);
	}

}
