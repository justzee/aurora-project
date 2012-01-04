package aurora.ide.meta.gef.editors.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import aurora.ide.meta.gef.editors.property.ComboPropertyDescriptor;

public class Grid extends GridColumn {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3083738388276859573L;
	public static final String SELECT_NONE = "";
	public static final String SELECT_MULTI = "multiple";
	public static final String SELECT_SINGLE = "single";
	public static final String SELECTION_MODE = "SelectionMode";
	private static final String[] selectionMode = { SELECT_NONE, SELECT_MULTI,
			SELECT_SINGLE };
	private GridSelectionCol gsc = new GridSelectionCol();

	public static final String NAVBAR_NONE = "";
	public static final String NAVBAR_SIMPLE = "simple";
	public static final String NAVBAR_COMPLEX = "complex";
	private static final String[] navBarTypes = { NAVBAR_NONE, NAVBAR_SIMPLE,
			NAVBAR_COMPLEX };
	private Navbar navBar = new Navbar();

	private static final IPropertyDescriptor PD_SELECTION_MODE = new ComboPropertyDescriptor(
			SELECTION_MODE, "SelectionMode", selectionMode);
	private static final IPropertyDescriptor PD_NAVBAR_TYPE = new ComboPropertyDescriptor(
			NAVBAR_TYPE, "NavBarType", navBarTypes);
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_WIDTH, PD_HEIGHT, PD_SELECTION_MODE, PD_NAVBAR_TYPE };

	private Toolbar toolbar;

	public Grid() {
		super();
		this.setSize(new Dimension(800, 380));
		ResultDataSet dataset = new ResultDataSet();
		dataset.setUseParentBM(false);
		this.setDataset(dataset);
		this.setType("grid");
	}

	public String getSelectionMode() {
		return gsc.getSelectionMode();
	}

	public ResultDataSet getDataset() {
		return (ResultDataSet) super.getDataset();
	}

	public void setSelectionMode(String sm) {
		if (gsc.getSelectionMode().equals(sm))
			return;
		gsc.setSelectionMode(sm);
		getDataset().setSelectionModel(sm);
		getDataset().setSelectable(!sm.equals(SELECT_NONE));
		if (gsc.getSelectionMode().equals(SELECT_NONE)) {
			removeChild(gsc);
		} else {
			int idx = getChildren().indexOf(gsc);
			if (idx == -1)
				addChild(gsc, 0);
		}
	}

	public void setNavbarType(String type) {
		if (eq(navBar.getType(), type))
			return;
		navBar.setType(type);
		if (eq(navBar.getType(), NAVBAR_NONE)) {
			removeChild(navBar);
		} else {
			int idx = getChildren().indexOf(navBar);
			if (idx == -1) {
				addChild(navBar, getChildren().size());
			}
		}
	}

	public boolean hasToolbar() {
		return getToolbar() != null;
	}

	public Toolbar getToolbar() {
		if (toolbar != null)
			return toolbar;
		else
			return (Toolbar) getFirstChild(Toolbar.class);
	}

	public void setToolbar(Toolbar tl) {
		this.toolbar = tl;
		this.addChild(tl);
	}

	@SuppressWarnings("unchecked")
	public List<Button> getToobarButtons() {
		return this.getToolbar() != null ? getToolbar().getButtons()
				: Collections.EMPTY_LIST;
	}

	@Override
	public boolean isResponsibleChild(AuroraComponent child) {
		if (child instanceof Toolbar || child instanceof Navbar)
			return this.getFirstChild(child.getClass()) == null;
		else if (child instanceof GridSelectionCol)
			return true;
		return super.isResponsibleChild(child);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

	@Override
	public Object getPropertyValue(Object propName) {
		if (SELECTION_MODE.equals(propName))
			return Arrays.asList(selectionMode).indexOf(getSelectionMode());
		else if (NAVBAR_TYPE.equals(propName))
			return Arrays.asList(navBarTypes).indexOf(navBar.getType());
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (SELECTION_MODE.equals(propName))
			setSelectionMode(selectionMode[(Integer) val]);
		else if (NAVBAR_TYPE.equals(propName))
			setNavbarType(navBarTypes[(Integer) val]);
		super.setPropertyValue(propName, val);
	}
}
