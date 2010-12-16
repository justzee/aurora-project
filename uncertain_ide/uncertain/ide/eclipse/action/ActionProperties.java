/**
 * 
 */
package uncertain.ide.eclipse.action;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.core.IViewer;

/**
 * @author linjinxiao
 *
 */
public class ActionProperties {
	protected IViewer viewer;
	protected CompositeMap parent;
	public ActionProperties(IViewer viewer, CompositeMap parent) {
		this.viewer = viewer;
		this.parent = parent;
	}
	public IViewer getViewer() {
		return viewer;
	}

	public void setViewer(IViewer viewer) {
		this.viewer = viewer;
	}

	public CompositeMap getParent() {
		return parent;
	}

	public void setParent(CompositeMap parent) {
		this.parent = parent;
	}
}
