package aurora.ide.meta.gef.editors.models;

public class BOX extends Container {
	private int row = 3;
	private int col = 3;
	private String title;
	private int labelWidth = 80;
	private String type;
	
	public static String FORM = "form";
	public static String HBOX = "hbox";
	public static String VBOX= "vbox";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8776030333465182289L;

	public int getRow() {
		return row;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRow(int row) {

		if (this.row == row) {
			return;
		}
		int old = this.row;
		this.row = row;
		firePropertyChange(ROW, old, row);
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {

		if (this.col == col) {
			return;
		}
		int old = this.col;
		this.col = col;
		firePropertyChange(COL, old, col);
	}

	public String getTitle() {
		return title;
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {


		if (this.labelWidth ==labelWidth) {
			return;
		}
		int old = this.labelWidth;
		this.labelWidth = labelWidth;
		firePropertyChange(LABELWIDTH, old, labelWidth);

	
		
		
		this.labelWidth = labelWidth;
	}

	public void setTitle(String title) {

		if (this.title.equals(title)) {
			return;
		}
		String old = this.title;
		this.title = title;
		firePropertyChange(TITLE, old, title);

	}

	public int getHeadHight() {

		return 0;
	}

}
