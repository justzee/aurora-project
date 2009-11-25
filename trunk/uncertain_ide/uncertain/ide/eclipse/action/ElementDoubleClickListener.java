package uncertain.ide.eclipse.action;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.AuroraShell;

public class ElementDoubleClickListener implements IDoubleClickListener {
	IDirty viewer;
	public ElementDoubleClickListener(IDirty viewer){
		this.viewer = viewer;
	}
	
	public void doubleClick(DoubleClickEvent event) {
		 TreeSelection selection = (TreeSelection) event.getSelection();
		 CompositeMap data = (CompositeMap) selection.getFirstElement();
//		final CompositeMap data = mSlectDataCm;
		if (data.getChilds() != null && data.getChilds().size() != 0) {

			final CompositeMap oldCopyData = new CompositeMap(data);
			// System.out.println("oldCopyData:"+oldCopyData.toXML());
			AuroraShell editor = new AuroraShell(viewer,data);
			// editor.start();
			Shell shell = new Shell(SWT.MIN | SWT.MAX | SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);
			shell.setLayout(new FillLayout());
			CompositeMap parent = data.getParent();
			String path = "";
			while (parent != null) {
				if (parent.getRawName() != null)
					path = parent.getRawName() + "/" + path;
				parent = parent.getParent();
			}
			path = path + data.getRawName();
			shell.setText(path);
//			shell.addDisposeListener(new DisposeListener() {
//
//				public void widgetDisposed(DisposeEvent e) {
//					// System.out.println("now data:"+data.toXML());
//					if (!data.toXML().equals(oldCopyData.toXML())) {
//						mServiceTreeEditor.refresh();
//						makeDirty();
//						// Shell shell = new Shell();
//						// MessageBox messageBox = new MessageBox(shell,
//						// SWT.ICON_WARNING | SWT.OK);
//						// messageBox.setText("信息");
//						// messageBox.setMessage("已经做了修改。");
//						// messageBox.open();
//					} else {
//						// Shell shell = new Shell();
//						// MessageBox messageBox = new MessageBox(shell,
//						// SWT.ICON_WARNING | SWT.OK);
//						// messageBox.setText("信息");
//						// messageBox.setMessage("没做修改。");
//						// messageBox.open();
//					}
//
//				}
//
//			});

			editor.createFormContent(shell);
			shell.open();

		}
	}

}
