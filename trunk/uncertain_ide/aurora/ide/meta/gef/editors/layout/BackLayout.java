package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.ide.meta.gef.editors.parts.ComponentPart;

public class BackLayout {
	protected static final Insets PADDING = new Insets(8, 16, 8, 6);//8,6,8,6

	public Rectangle layout(ComponentPart ep) {
		Rectangle bounds = ep.getComponent().getBounds();
		return bounds;
	}

	protected Rectangle newChildLocation(Rectangle layout) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		ep.getFigure().setBounds(layout);
		
	}
}
