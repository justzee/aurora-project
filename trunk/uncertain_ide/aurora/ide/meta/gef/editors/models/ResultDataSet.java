package aurora.ide.meta.gef.editors.models;

public class ResultDataSet extends Dataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4436804459187661221L;

	private AuroraComponent queryContainer;

	public static final int DEFAULT_PAGE_SIZE = 10;
	private int pageSize;
	private boolean selectable;

	// private String selectionModel="multiple"/"single" ;
	private String selectionModel;

	public ResultDataSet() {
		this.setUse4Query(false);
		this.setPageSize(DEFAULT_PAGE_SIZE);
	}

	public AuroraComponent getQueryContainer() {
		return queryContainer;
	}

	public void setQueryContainer(AuroraComponent queryContainer) {
		this.queryContainer = queryContainer;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public String getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(String selectionModel) {
		this.selectionModel = selectionModel;
	}

}