package aurora.ide.meta.gef.editors.models;

import org.eclipse.draw2d.geometry.Dimension;

public class HBox extends BOX {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6675954637550725095L;

	public HBox() {
		setSize(new Dimension(600, 40));
	}

	public int getHeadHight() {

		return 5;
	}

	@Override
	final public int getRow() {
		// always 1
		return 1;
	}

	@Override
	final public void setRow(int row) {
		// always 1
	}

	@Override
	final public int getCol() {
		return 1000;
	}

	@Override
	final public void setCol(int col) {
		// always Integer.MAX_VALUE
	}
}
