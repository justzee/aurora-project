package uncertain.ide.eclipse.action;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.eclipse.editor.ActionLabelManager;

public 	class RemoveElementAction extends Action {
	IViewerDirty viewer;
	public RemoveElementAction(IViewerDirty viewer) {
		// 正常情况下的图标
//		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
//		setText("删除子节点");
		this.viewer = viewer;
	}
	public RemoveElementAction(IViewerDirty viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	/**
	 * 这里演示了如何从表格中删除所选的记录（可选多个）
	 */
	public void run() {
		CompositeMapAction.removeElement(viewer);

	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return ActionLabelManager.getImageDescriptor(ActionLabelManager.DELETE);
	}
	public static String getDefaultText(){
		return ActionLabelManager.getText(ActionLabelManager.DELETE);
	}
}
