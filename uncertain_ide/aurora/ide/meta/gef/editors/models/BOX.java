package aurora.ide.meta.gef.editors.models;

public class BOX extends RowCol {
	
	private String title;
	private int labelWidth = 80;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8776030333465182289L;



	public String getTitle() {
		return title;
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		if (this.labelWidth == labelWidth) {
			return;
		}
		int old = this.labelWidth;
		this.labelWidth = labelWidth;
		firePropertyChange(LABELWIDTH, old, labelWidth);
		this.labelWidth = labelWidth;
	}

	public void setTitle(String title) {
		if (eq(this.title, title)) {
			return;
		}
		String old = this.title;
		this.title = title;
		firePropertyChange(TITLE, old, title);

	}

	public boolean isResponsibleChild(AuroraComponent component) {
		if (component instanceof Grid)
			return true;
		if (component instanceof Toolbar || component instanceof Navbar
				|| component instanceof GridColumn)
			return false;
		return super.isResponsibleChild(component);
	}

}
