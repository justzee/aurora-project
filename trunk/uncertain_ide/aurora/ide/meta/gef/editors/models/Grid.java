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
	public static final String SELECTIONMODE = "SELECTIONMMODE";
	private static final String[] selectionMode = { SELECT_NONE, SELECT_MULTI,
			SELECT_SINGLE };
	private Toolbar toolbar;
	private Navbar navBar;

	private static final IPropertyDescriptor PD_SELECTIONMODE = new ComboPropertyDescriptor(
			SELECTIONMODE, "SelectionMode", selectionMode);

	private GridSelectionCol gsc = new GridSelectionCol();
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_WIDTH, PD_HEIGHT, PD_SELECTIONMODE };

	public Grid() {
		super();
		this.setSize(new Dimension(800, 380));
		ResultDataSet dataset = new ResultDataSet();
		dataset.setUseParentBM(false);
		this.setDataset(dataset);
	}

	public String getSelectionMode() {
		return gsc.getSelectionMode();
	}

	public void setSelectionMode(String sm) {
		if (gsc.getSelectionMode().equals(sm))
			return;
		gsc.setSelectionMode(sm);
		if (gsc.getSelectionMode().equals(SELECT_NONE)) {
			removeChild(gsc);
		} else {
			int idx = getChildren().indexOf(gsc);
			if (idx == -1)
				addChild(gsc, 0);
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

	public boolean hasNavBar() {
		return getNavbar() != null;
	}

	public Navbar getNavbar() {
		if (navBar != null)
			return navBar;
		else
			return (Navbar) getFirstChild(Navbar.class);
	}

	public void setNavBar(Navbar nb) {
		this.navBar = nb;
		this.addChild(nb);
	}

	public String getNavBarType() {
		return getNavbar() == null ? "" : getNavbar().getType();
	}

	public void setNavBarType(String navBarType) {
		if (getNavbar() != null) {
			getNavbar().setType(navBarType);
		}
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
		if (SELECTIONMODE.equals(propName))
			return Arrays.asList(selectionMode).indexOf(getSelectionMode());
		return super.getPropertyValue(propName);
	}

	@Override
	public void setPropertyValue(Object propName, Object val) {
		if (SELECTIONMODE.equals(propName))
			setSelectionMode(selectionMode[(Integer) val]);
		super.setPropertyValue(propName, val);
	}
}
