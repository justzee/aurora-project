package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.eclipse.editor.ActionLabelManager;

/**
 * 刷新的Action类
 */
public class RefreshAction extends Action {
	IViewer viewer;
	public RefreshAction(IViewer viewer) {
		// 给Action设置图像。getImageDesc为自定义方法，得到一个图像
		// setHoverImageDescriptor(getImageDesc("refresh.gif"));
//		setImageDescriptor(getImageDescriptor());
//		setText("刷新");
		this.viewer = viewer;
	}
	public RefreshAction(IViewer viewer,ImageDescriptor imageDescriptor,String text) {
		// 给Action设置图像。getImageDesc为自定义方法，得到一个图像
		// setHoverImageDescriptor(getImageDesc("refresh.gif"));
		if(imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		viewer.refresh(false);// 调用表格的刷新方法
	}

	public static ImageDescriptor getDefaultImageDescriptor(){
		return ActionLabelManager.getImageDescriptor(ActionLabelManager.REFRESH);
	}
}
