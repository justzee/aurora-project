package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import aurora_ide.Activator;

import uncertain.ide.eclipse.editor.service.ServicePropertyEditor;

public class CategroyAction extends Action {
	
	private IPropertyCategory mDirtyAction;
	public CategroyAction(IPropertyCategory dirtyObject) {
		// 正常情况下的图标
		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
		setText("分组显示");
		mDirtyAction = dirtyObject;
	}

	public void run() {
		mDirtyAction.setIsCategory(true);
		mDirtyAction.refresh();
	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/category.gif");
		return imageDescriptor;
	}
}
