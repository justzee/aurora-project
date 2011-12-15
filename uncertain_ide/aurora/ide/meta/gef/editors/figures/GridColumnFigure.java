package aurora.ide.meta.gef.editors.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.GridColumn;

public class GridColumnFigure extends Figure {

	private int labelWidth;

	private int columnHight = 25;

	private GridColumn gridColumn;

	public GridColumnFigure() {
		this.setLayoutManager(new DummyLayout());
		this.setBorder(new GridColumnBorder("prompt", "grid_bg", this));
	}

	public int getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(int labelWidth) {
		this.labelWidth = labelWidth;
	}

	@Override
	public void handleFocusGained(FocusEvent event) {
		super.handleFocusGained(event);
	
	}

	/**
	 * @see org.eclipse.draw2d.Label#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Rectangle copy = this.getBounds().getCopy();

		if (this.getChildren().size() > 0) {
			return;
		}
		int k = 1;
		for (int i = copy.y + columnHight; i < copy.y + copy.height; i += 25) {
			if (k % 2 == 0) {
				graphics.setBackgroundColor(ColorConstants.GRID_ROW);
				graphics.fillRectangle(copy.x, i, copy.width, 25);
			}
			graphics.setForegroundColor(ColorConstants.GRID_COLUMN_GRAY);
			graphics.drawLine(copy.x, i, copy.x + copy.width, i);
			k++;
		}
		// super.paintFigure(graphics);
	}

	
	public void setModel(GridColumn component) {
		this.gridColumn = component;

	}

	public int getColumnHight() {

		return columnHight;
	}

	public void setColumnHight(int columnHight) {
		this.columnHight = columnHight;
		this.repaint();
	}

}
