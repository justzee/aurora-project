package aurora.ide.meta.gef.editors.models;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

public class ResultDataSet extends Dataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4436804459187661221L;

	private AuroraComponent queryContainer;

	public static final int DEFAULT_PAGE_SIZE = 10;
	private int pageSize;
	private boolean selectable = false;

	// private String selectionModel="multiple"/"single" ;
	private String selectionMode = Grid.SELECT_NONE;

	public static final String SELECTION_MODE = "selectionMode";
	public static final String PAGE_SIZE = "pageSize";
	public static final String SELECTABLE = "selectable";
	public static final String QUER_DATASET = "queryDataSet";

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			new StringPropertyDescriptor(AUTO_QUERY, "autoQuery"),
			new StringPropertyDescriptor(MODEL, "model"),
			new StringPropertyDescriptor(ID, "id"),
			new StringPropertyDescriptor(SELECTION_MODE, "selectionMode"),
			new StringPropertyDescriptor(PAGE_SIZE, "pageSize"),
			new StringPropertyDescriptor(SELECTABLE, "selectable"),
			new StringPropertyDescriptor(QUER_DATASET, "queryDataSet") };

	public ResultDataSet() {
		this.setUse4Query(false);
		this.setPageSize(DEFAULT_PAGE_SIZE);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (SELECTION_MODE.equals(propName)) {
			return this.getSelectionMode();
		}

		if (PAGE_SIZE.equals(propName)) {
			return this.getPageSize();
		}

		if (SELECTABLE.equals(propName)) {
			return this.isSelectable();
		}

		if (QUER_DATASET.equals(propName)) {
			return this.getQueryDataset();
		}
		return super.getPropertyValue(propName);
	}

	private String getQueryDataset() {
		return ((Container) this.getQueryContainer()).getDataset().getId();
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

	public String getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(String selectionMode) {
		this.selectionMode = selectionMode;
	}

}