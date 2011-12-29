package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class GridColumn extends RowCol {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3032139528088861361L;
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_WIDTH };

	private List<GridColumn> cols = new ArrayList<GridColumn>();
	// 界面默认的行高 25
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

	public GridColumn() {
		super();
		this.row = 1;
		this.col = 999;
		this.headHight = 25;
		this.setSize(new Dimension(100, rowHight * 2 + 10));
	}

	public void addCol(GridColumn col) {
		cols.add(col);
		this.addChild(col);
	}

	/**
	 * 
	 * 仅允许增加 GridColumn
	 * */
	public boolean isResponsibleChild(AuroraComponent child) {
		return child instanceof GridColumn;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

}
