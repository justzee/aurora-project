package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.GridColumnPart;
import aurora.ide.meta.gef.editors.parts.GridPart;
import aurora.ide.meta.gef.editors.parts.InputPart;
import aurora.ide.meta.gef.editors.parts.NavbarPart;
import aurora.ide.meta.gef.editors.parts.ToolbarPart;

public class GraphLayoutManager {

	static BackLayout createLayout(ComponentPart ep) {
		if (ep instanceof BoxPart) {
			return new BoxBackLayout();
		}
		if(ep instanceof InputPart){
			return new InputFieldLayout();
		}
		if(ep instanceof GridPart){
			return new GridBackLayout();
		}
		if(ep instanceof GridColumnPart){
			return new GridColumnBackLayout();
		}
		if(ep instanceof ToolbarPart){
			return new ToolbarBackLayout();
		}
		if(ep instanceof NavbarPart){
			return new ToolbarBackLayout();
		}
		return new BackLayout();
	}

	static public Rectangle layout(ComponentPart ep) {
		return createLayout(ep).layout(ep);
	}

}
