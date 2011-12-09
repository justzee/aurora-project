package aurora.ide.meta.gef.editors.layout;

import org.eclipse.draw2d.geometry.Rectangle;

import aurora.ide.meta.gef.editors.parts.BoxPart;
import aurora.ide.meta.gef.editors.parts.ComponentPart;
import aurora.ide.meta.gef.editors.parts.InputPart;

public class GraphLayoutManager {

	static BackLayout createLayout(ComponentPart ep) {
		if (ep instanceof BoxPart) {
			return new BoxBackLayout();
		}
		if(ep instanceof InputPart){
			return new InputFieldLayout();
		}
		return new BackLayout();
	}

	static public Rectangle layout(ComponentPart ep) {
		return createLayout(ep).layout(ep);
	}

}
