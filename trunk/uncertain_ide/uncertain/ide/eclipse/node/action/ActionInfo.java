/**
 * 
 */
package uncertain.ide.eclipse.node.action;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.core.IViewer;

/**
 * @author linjinxiao
 *
 */
public class ActionInfo {
	protected IViewer viewer;
	protected CompositeMap currentNode;
	public ActionInfo(IViewer viewer, CompositeMap currentNode) {
		this.viewer = viewer;
		this.currentNode = currentNode;
	}
	public IViewer getViewer() {
		return viewer;
	}

	public void setViewer(IViewer viewer) {
		this.viewer = viewer;
	}

	public CompositeMap getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(CompositeMap currentNode) {
		this.currentNode = currentNode;
	}
}
