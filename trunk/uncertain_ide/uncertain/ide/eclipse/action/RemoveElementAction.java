package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.schema.Element;

import aurora_ide.Activator;

public 	class RemoveElementAction extends Action {
	IViewerDirty mDirtyObject;
	public RemoveElementAction(IViewerDirty dirtyObject) {
		// 正常情况下的图标
		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
		setText("删除子节点");
		mDirtyObject = dirtyObject;
	}

	/**
	 * 这里演示了如何从表格中删除所选的记录（可选多个）
	 */
	public void run() {
		CompositeMapAction.removeElement(mDirtyObject);

	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/delete_obj.gif");
		return imageDescriptor;
	}
}
