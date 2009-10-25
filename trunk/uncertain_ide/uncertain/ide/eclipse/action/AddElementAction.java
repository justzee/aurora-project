package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.graphics.Image;

import aurora_ide.Activator;

import uncertain.composite.CompositeMap;

public 	class AddElementAction extends Action {
	private IViewerDirty viewerDirty;

	CompositeMap parentCM;
	String prefix;
	String uri;
	String name;

	public AddElementAction(IViewerDirty mDirtyObject, CompositeMap parentCM, String _prefix,
			String _uri, String _name) {
		this.viewerDirty = mDirtyObject;
		this.parentCM = parentCM;
		this.prefix = _prefix;
		this.uri = _uri;
		this.name = _name;
		// 正常情况下的图标
		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
		setText(_name);
	}

	/**
	 * 这里演示了如何从表格中删除所选的记录（可选多个）
	 */
	public void run() {
		CompositeMapAction.addElement(parentCM, prefix, uri, name);
		if (viewerDirty != null) {
			viewerDirty.setDirty(true);
			viewerDirty.refresh();
		}
	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/element_obj.gif");
		return imageDescriptor;
	}
}
