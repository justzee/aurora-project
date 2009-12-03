package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.ActionLabelManager;

public 	class CopyAction extends Action {
	IViewerDirty viewer;
	public CopyAction(IViewerDirty viewer) {
		this.viewer = viewer;
	}
	public CopyAction(IViewerDirty viewer,ImageDescriptor imageDescriptor,String text) {
		if(imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap cm = new CompositeMap((CompositeMap) obj);
		CompositeMap cm = new CompositeMap(viewer.getFocusData());
		viewer.setSelectedData(cm);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return ActionLabelManager.getImageDescriptor(ActionLabelManager.COPY);
	}
	public static String getDefaultText(){
		return ActionLabelManager.getText(ActionLabelManager.COPY);
	}
	
}
