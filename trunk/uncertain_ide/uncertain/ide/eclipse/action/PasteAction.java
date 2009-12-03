package uncertain.ide.eclipse.action;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.ActionLabelManager;
import uncertain.schema.Element;

public 	class PasteAction extends Action {
	IViewerDirty viewer;
	public PasteAction(IViewerDirty dirtyObject) {
//		this.setHoverImageDescriptor(getImageDescriptor());
		// setAccelerator(SWT.CTRL+'V');
//		setText("粘贴");
		viewer = dirtyObject;
	}
	public PasteAction(IViewerDirty viewer,ImageDescriptor imageDescriptor,String text) {
		this.setHoverImageDescriptor(getImageDescriptor());
		if(imageDescriptor != null)
			setImageDescriptor(imageDescriptor);
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		if (viewer.getSelectedData() == null)
			return;
//		ISelection selection = mDirtyObject.getObject().getSelection();
//		Object obj = ((IStructuredSelection) selection).getFirstElement();
//		CompositeMap parentComp = (CompositeMap) obj;
		CompositeMap parentComp = viewer.getFocusData();
		CompositeMap child = viewer.getSelectedData();
		
		if (child != null) {
			parentComp.addChild(child);
			Element em = Activator.getSchemaManager().getElement(child);
			if (em == null) {
				parentComp.removeChild(child);
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
						| SWT.OK);
				messageBox.setText("Warning");
				messageBox.setMessage("此节点不能包含此子节点.");
				messageBox.open();
				return;
			} else if (viewer.getSelectedData().getParent() != null)
				viewer.getSelectedData().getParent().removeChild(viewer.getSelectedData());
		}
		
		viewer.setSelectedData(null);
		if (child != null)
			parentComp.addChild(child);
		viewer.refresh(true);
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return ActionLabelManager.getImageDescriptor(ActionLabelManager.PASTE);
	}
	public static String getDefaultText(){
		return ActionLabelManager.getText(ActionLabelManager.PASTE);
	}
}
