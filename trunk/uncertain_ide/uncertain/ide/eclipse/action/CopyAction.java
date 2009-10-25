package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import aurora_ide.Activator;

import uncertain.composite.CompositeMap;

public 	class CopyAction extends Action {
	IViewerDirty mDirtyObject;
	public CopyAction(IViewerDirty dirtyObject) {
		setHoverImageDescriptor(getImageDescriptor());
		setText("¸´ÖÆ");
		mDirtyObject = dirtyObject;
	}

	public void run() {
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap cm = new CompositeMap((CompositeMap) obj);
		CompositeMap cm = mDirtyObject.getFocusData();
		mDirtyObject.setSelectedData(cm);
	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/copy.gif");
		return imageDescriptor;
	}
}
