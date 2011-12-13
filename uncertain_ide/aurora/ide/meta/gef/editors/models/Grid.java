package aurora.ide.meta.gef.editors.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;

public class Grid extends GridColumn {
	
	private boolean hasToolbar;
	// TODO button?
	private List<String> toobarButtons = new ArrayList<String>();
	private boolean hasNavBar;
	private String navBarType;

	

	public Grid() {
		super();
		this.setSize(new Dimension(800, 380));
	}


	public boolean isHasToolbar() {
		return hasToolbar;
	}

	public void setHasToolbar(boolean hasToolbar) {
		this.hasToolbar = hasToolbar;
		if (this.hasToolbar == hasToolbar) {
			return;
		}
		boolean old = this.hasToolbar;
		this.hasToolbar = hasToolbar;
		firePropertyChange(HAS_TOOLBAR, old, hasToolbar);
	}

	public List<String> getToobarButtons() {
		return toobarButtons;
	}

	public void addToobarButton(String type) {
		toobarButtons.add(type);
		firePropertyChange(ADD_TOOLBAR_BUTTON, null, type);
	}

	public void removeToobarButton(String type) {
		toobarButtons.remove(type);
		firePropertyChange(REMOVE_TOOLBAR_BUTTON, type, null);
	}

	public boolean isHasNavBar() {
		return hasNavBar;
	}

	public void setHasNavBar(boolean hasNavBar) {
		this.hasNavBar = hasNavBar;
		if (this.hasNavBar == hasNavBar) {
			return;
		}
		boolean old = this.hasNavBar;
		this.hasNavBar = hasNavBar;
		firePropertyChange(HAS_NAVBAR, old, hasNavBar);
	}

	public String getNavBarType() {
		return navBarType;
	}

	public void setNavBarType(String navBarType) {
		this.navBarType = navBarType;
		if (this.navBarType.equals(navBarType)) {
			return;
		}
		String old = this.navBarType;
		this.navBarType = navBarType;
		firePropertyChange(NAVBAR_TYPE, old, navBarType);
	}
}
