package aurora.ide.meta.gef.editors.parts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import aurora.ide.meta.gef.editors.figures.NavbarFigure;

public class NavbarPart extends ContainerPart {

	@Override
	protected IFigure createFigure() {
		Figure figure = new NavbarFigure();

		return figure;
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
	}

	protected void refreshVisuals() {
		super.refreshVisuals();

	}

}
