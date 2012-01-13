package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.DialogEditableObject;

import org.eclipse.swt.graphics.Image;

public class Renderer extends AuroraComponent implements DialogEditableObject {

	private static final long serialVersionUID = -3218999047690358423L;
	private String openPath = "";
	private String labelText = "";
	private GridColumn column;

	public Renderer() {

	}

	public void setColumn(GridColumn col) {
		column = col;
	}

	public GridColumn getColumn() {
		return column;
	}

	public String getDescripition() {
		return labelText;
	}

	public Object getContextInfo() {
		return column;
	}

	public Renderer clone() {
		Renderer r = new Renderer();
		r.openPath = openPath;
		r.column = column;
		r.labelText = labelText;
		return r;
	}

	public String getOpenPath() {
		return openPath;
	}

	public void setOpenPath(String openPath) {
		this.openPath = openPath;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String tmpLabelText) {
		this.labelText = tmpLabelText;
	}

	public Image getDisplayImage() {
		return null;
	}

}
