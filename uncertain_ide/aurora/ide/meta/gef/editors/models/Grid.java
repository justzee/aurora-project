package aurora.ide.meta.gef.editors.models;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public class Grid extends GridColumn {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3083738388276859573L;
	private Toolbar toolbar;
	private Navbar navBar;
	private static final IPropertyDescriptor[] pds = new IPropertyDescriptor[] {
			PD_PROMPT, PD_WIDTH, PD_HEIGHT };

	public Grid() {
		super();
		this.setSize(new Dimension(800, 380));
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
		return super.isResponsibleChild(child);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pds;
	}

}
