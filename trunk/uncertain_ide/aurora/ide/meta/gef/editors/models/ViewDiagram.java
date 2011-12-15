package aurora.ide.meta.gef.editors.models;

public class ViewDiagram extends Container {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9196440587781890208L;
	public static final int DLabelWidth = 80;

	@Override
	public boolean isResponsibleChild(AuroraComponent component) {
		if (component instanceof Toolbar || component instanceof Navbar
				|| component instanceof GridColumn)
			return false;
		return super.isResponsibleChild(component);
	}

}
