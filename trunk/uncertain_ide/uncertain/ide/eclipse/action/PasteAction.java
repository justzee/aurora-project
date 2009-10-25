package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import aurora_ide.Activator;

import uncertain.composite.CompositeMap;

public 	class PasteAction extends Action {
	IViewerDirty mDirtyObject;
	public PasteAction(IViewerDirty dirtyObject) {
		this.setHoverImageDescriptor(getImageDescriptor());
		// setAccelerator(SWT.CTRL+'V');
		setText("Õ³Ìù");
		mDirtyObject = dirtyObject;
	}

	public void run() {
		if (mDirtyObject.getSelectedData() == null)
			return;
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap parentComp = (CompositeMap) obj;
		CompositeMap parentComp = mDirtyObject.getFocusData();
		CompositeMap child = mDirtyObject.getSelectedData();
		mDirtyObject.setSelectedData(null);
		if (child != null)
			parentComp.addChild(child);
		mDirtyObject.setDirty(true);
		mDirtyObject.refresh();
	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/paste.gif");
		return imageDescriptor;
	}
}
