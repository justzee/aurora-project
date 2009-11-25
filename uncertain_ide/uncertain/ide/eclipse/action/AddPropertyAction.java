package uncertain.ide.eclipse.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.ActionLabelManager;

public 	class AddPropertyAction extends Action {
	IPropertyCategory viewer;
	public AddPropertyAction(IPropertyCategory viewer) {
		this.viewer = viewer;
	}
	public AddPropertyAction(IPropertyCategory viewer,ImageDescriptor imageDescriptor,String text) {
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
//		final CompositeMap data = (CompositeMap) mDirtyObject.getObject().getInput();
		showInputDialog();
	}
	public static ImageDescriptor getDefaultImageDescriptor(){
		return ActionLabelManager.getImageDescriptor(ActionLabelManager.ADD);
	}
	private void showInputDialog(){
		final CompositeMap data = viewer.getInput();
		System.out.println(data.toXML());
		final Shell shell = new Shell();
		shell.setSize(400, 200);

		Label propertyLabe = new Label(shell, SWT.NONE);
		propertyLabe.setText("属性名:");
		propertyLabe.setBounds(20, 20, 50, 30);

		final Text propertyText = new Text(shell, SWT.SHADOW_IN);
		propertyText.setBounds(80, 20, 300, 20);

		Label valueLabel = new Label(shell, SWT.NONE);
		valueLabel.setText("值:");
		valueLabel.setBounds(20, 50, 50, 30);

		final Text valueText = new Text(shell, SWT.SHADOW_IN);
		valueText.setBounds(80, 50, 300, 20);

		final Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.setBounds(220, 120, 70, 25);

		final Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setBounds(300, 120, 70, 25);
		SelectionListener listener = getListener(data, shell, propertyText,
				valueText, ok, cancel);
		ok.addSelectionListener(listener);
		cancel.addSelectionListener(listener);
		shell.open();
	}


	private SelectionListener getListener(final CompositeMap data,
			final Shell shell, final Text propertyText, final Text valueText,
			final Button ok, final Button cancel) {
		SelectionListener listener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
			public void widgetSelected(SelectionEvent e) {
				Widget w = e.widget;
				if (w == ok) {
					System.out.println("ok........");
					System.out.println(propertyText.getText() + ":"
							+ valueText.getText());
					data.put(propertyText.getText(), valueText.getText());
					System.out.println(data.toXML());
					viewer.refresh(true);
					shell.dispose();
				} else if (w == cancel) {
					shell.dispose();
				}

			}
		};
		return listener;
	}
}

