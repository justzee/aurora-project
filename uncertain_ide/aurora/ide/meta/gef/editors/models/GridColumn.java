package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class GridColumn extends Container {
	
	private List<GridColumn> cols = new ArrayList<GridColumn>();

	private int rowHight = 25;

	public int getRowHight() {
		return rowHight;
	}

	public void setRowHight(int rowHight) {
		this.rowHight = rowHight;
	}

	public List<GridColumn> getCols() {
		return cols;
	}

	public List<GridColumn> getColNames() {
		return cols;
	}

	public GridColumn() {
		super();
		this.setSize(new Dimension(100, rowHight*2+10));
	}

	public void removeCol(GridColumn col) {
		cols.remove(col);
		firePropertyChange(REMOVE_COL, col, null);
	}

	public void addCol(GridColumn col) {
		cols.add(col);
		firePropertyChange(ADD_COl, null, col);
	}
	
}
