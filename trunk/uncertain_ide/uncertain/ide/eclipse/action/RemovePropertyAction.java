package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.ActionLabelManager;
import uncertain.schema.editor.AttributeValue;

public 	class RemovePropertyAction extends Action {
	
	IPropertyCategory viewer;
	public RemovePropertyAction(IPropertyCategory viewer) {
		// 正常情况下的图标
//		setHoverImageDescriptor(getImageDescriptor());
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
//		setText("删除属性");
		this.viewer = viewer;
	}
	public RemovePropertyAction(IPropertyCategory viewer,ImageDescriptor imageDescriptor,String text) {
		// 正常情况下的图标
		if(imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		// 置灰（removeAction.setEnabled(false)）情况下的图标
		// setDisabledImageDescriptor(getImageDesc("disremove.gif"));
		if(text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		Shell shell = new Shell();
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING
				| SWT.OK | SWT.CANCEL);
		messageBox.setText("Warning");
		messageBox.setMessage("确认删除此属性吗?");
		int buttonID = messageBox.open();
		switch (buttonID) {
		case SWT.OK:
//			final CompositeMap data = (CompositeMap) viewer.getObject()
//					.getInput();
//			System.out.println(data.toXML());
//			ISelection selection = viewer.getObject().getSelection();
//			Object obj = ((IStructuredSelection) selection)
//					.getFirstElement();
//			AttributeValue av = (AttributeValue) obj;
			final CompositeMap data = viewer.getInput();

			AttributeValue av = viewer.getFocusData();
			String propertyName = av.getAttribute().getLocalName();
			System.out.println(propertyName);
			data.remove(propertyName);
			viewer.refresh(true);
		case SWT.CANCEL:
			break;
		}

	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return ActionLabelManager.getImageDescriptor(ActionLabelManager.DELETE);
	}
}
