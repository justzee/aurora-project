package aurora.ide.meta.gef.editors.models;

public class VBox extends BOX {
	/**
	 * 
	 */
	private static final long serialVersionUID = 588460053081220683L;

	public int getHeadHight() {
		return 5;
	}

	@Override
	final public int getRow() {
		return Integer.MAX_VALUE;
	}

	@Override
	final public void setRow(int row) {
		// always Integer.MAX_VALUE
	}

	@Override
	final public int getCol() {
		// always 1
		return 1;
	}

	@Override
	final public void setCol(int col) {
		// always 1
	}
}
