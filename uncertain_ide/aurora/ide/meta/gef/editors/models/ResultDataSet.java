package aurora.ide.meta.gef.editors.models;

import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.IntegerPropertyDescriptor;
import aurora.ide.meta.gef.editors.property.StringPropertyDescriptor;

import java.util.Arrays;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class ResultDataSet extends Dataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4436804459187661221L;
	public static final String SELECT_NONE = "";
	public static final String SELECT_MULTI = "multiple";
	public static final String SELECT_SINGLE = "single";
	public static final String SELECTION_MODE = "selectionMode";
	private static final String[] selectionModes = { SELECT_NONE, SELECT_MULTI,
			SELECT_SINGLE };
	private static final IPropertyDescriptor PD_SELECTION_MODE = new ComboPropertyDescriptor(
			SELECTION_MODE, "SelectionMode", selectionModes);

	private AuroraComponent queryContainer;

	public static final int DEFAULT_PAGE_SIZE = 10;
	private int pageSize;
	private boolean selectable = false;

	// private String selectionModel="multiple"/"single" ;
	private String selectionMode = SELECT_NONE;

	public static final String PAGE_SIZE = "pageSize";
	public static final String SELECTABLE = "selectable";
	public static final String QUERY_DATASET = "queryDataSet";

	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_SELECTION_MODE,
			new IntegerPropertyDescriptor(PAGE_SIZE, "pageSize"),
			// new BooleanPropertyDescriptor(SELECTABLE, "selectable"),
			new StringPropertyDescriptor(QUERY_DATASET, "queryDataSet") };

	public ResultDataSet() {
		this.setUse4Query(false);
		this.setPageSize(DEFAULT_PAGE_SIZE);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return mergePropertyDescriptor(super.getPropertyDescriptors(), pds);
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (SELECTION_MODE.equals(propName))
			return Arrays.asList(selectionModes).indexOf(getSelectionMode());
		else if (PAGE_SIZE.equals(propName)) {
			return this.getPageSize();
		} else if (SELECTABLE.equals(propName)) {
			return this.isSelectable();
		} else if (QUERY_DATASET.equals(propName)) {
			return this.getQueryContainer();
		}
		return super.getPropertyValue(propName);
	}

	public void setPropertyValue(Object propName, Object val) {
		if (SELECTION_MODE.equals(propName))
			setSelectionMode(selectionModes[(Integer) val]);
		else if (PAGE_SIZE.equals(propName)) {
			setPageSize((Integer) val);
		} else if (SELECTABLE.equals(propName)) {
			setSelectable((Boolean) val);
		} else if (QUERY_DATASET.equals(propName)) {
			setQueryContainer((AuroraComponent) val);
		} else
			super.setPropertyValue(propName, val);
	}

	private String getQueryDataset() {
		if (this.getQueryContainer() instanceof Container)
			return ((Container) this.getQueryContainer()).getDataset().getId();
		return "";
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
		setSelectable(!selectionMode.equals(SELECT_NONE));
	}

}