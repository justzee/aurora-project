package aurora.ide.meta.gef.editors.layout;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.models.TabItem;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.TabBodyPart;
import aurora.ide.meta.gef.editors.parts.TabFolderPart;
import aurora.ide.meta.gef.editors.parts.TabItemPart;

public class TabFolderLayout extends BackLayout {

	@Override
	public Rectangle layout(ComponentPart ep) {
		TabFolderPart part = (TabFolderPart) ep;
		Point pos = part.getFigure().getBounds().getTopLeft().translate(2, 2);
		@SuppressWarnings("unchecked")
		List<ComponentPart> list = part.getChildren();
		for (ComponentPart cp : list) {
			if (cp instanceof TabItemPart) {
				TabItemPart tip = (TabItemPart) cp;
				Rectangle bounds = tip.getModel().getBounds();
				bounds.setLocation(pos);
				pos.x += bounds.width + 2;
				tip.getFigure().setBounds(bounds);
			} else if (cp instanceof TabBodyPart) {
				Rectangle rect = part.getFigure().getBounds().getCopy();
				rect.y += TabItem.HEIGHT + 2;
				rect.height -= TabItem.HEIGHT + 2;
				cp.getFigure().setBounds(rect);
			}
		}
		return super.layout(ep);
	}

	@Override
	protected void applyToFigure(ComponentPart ep, Rectangle layout) {
		super.applyToFigure(ep, layout);
	}

}
