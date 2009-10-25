package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import aurora_ide.Activator;

/**
 * 刷新的Action类
 */
public class RefreshAction extends Action {
	IViewerDirty mDirtyObject;
	public RefreshAction(IViewerDirty dirtyAction) {
		// 给Action设置图像。getImageDesc为自定义方法，得到一个图像
		// setHoverImageDescriptor(getImageDesc("refresh.gif"));
		setImageDescriptor(getImageDescriptor());
		setText("刷新");
		mDirtyObject = dirtyAction;
	}

	public void run() {
		mDirtyObject.refresh();// 调用表格的刷新方法
	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/refresh.gif");
		return imageDescriptor;
	}
}
