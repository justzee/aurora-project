package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import aurora_ide.Activator;

import uncertain.ide.eclipse.editor.service.ServicePropertyEditor;

public class CharSortAction extends Action {
	
	private IPropertyCategory mDirtyAction;
	public CharSortAction(IPropertyCategory dirtyObject) {
		// 正常情况下的图标
		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
		setText("A-Z排序");
		mDirtyAction = dirtyObject;
	}

	/**
	 * 这里演示了如何从表格中删除所选的记录（可选多个）
	 */
	public void run() {
		mDirtyAction.setIsCategory(false);
		mDirtyAction.refresh();
	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/asc.gif");
		return imageDescriptor;
	}
}
