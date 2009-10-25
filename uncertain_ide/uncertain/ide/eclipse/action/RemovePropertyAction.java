package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import aurora_ide.Activator;

import uncertain.composite.CompositeMap;
import uncertain.schema.editor.AttributeValue;

public 	class RemovePropertyAction extends Action {
	
	IViewerDirty mDirtyObject;
	public RemovePropertyAction(IViewerDirty dirtyAction) {
		// 正常情况下的图标
		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
		setText("删除属性");
	}

	/**
	 * 这里演示了如何从表格中删除所选的记录（可选多个）
	 */
	public void run() {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
				| SWT.OK | SWT.CANCEL);
		messageBox.setText("Warning");
		messageBox.setMessage("确认删除此属性吗?");
		int buttonID = messageBox.open();
		switch (buttonID) {
		case SWT.OK:
			final CompositeMap data = (CompositeMap) mDirtyObject.getObject()
					.getInput();
			System.out.println(data.toXML());
			ISelection selection = mDirtyObject.getObject().getSelection();
			Object obj = ((IStructuredSelection) selection)
					.getFirstElement();
			AttributeValue av = (AttributeValue) obj;
			String propertyName = av.getAttribute().getLocalName();
			System.out.println(propertyName);
			data.remove(propertyName);

			makeDirty();
			mDirtyObject.refresh();
		case SWT.CANCEL:
			break;
		}

	}
	public ImageDescriptor getImageDescriptor(){
		ImageDescriptor imageDescriptor = Activator.getImageDescriptor("icons/delete_obj.gif");
		return imageDescriptor;
	}
	private void makeDirty(){
		mDirtyObject.setDirty(true);
	}
}
