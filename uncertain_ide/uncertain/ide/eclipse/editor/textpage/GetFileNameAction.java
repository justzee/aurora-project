package uncertain.ide.eclipse.editor.textpage;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import uncertain.ide.help.CustomDialog;

public class GetFileNameAction implements IEditorActionDelegate {

	IEditorPart activeEditor;
	public GetFileNameAction() {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		activeEditor = targetEditor;
	}

	public void run(IAction action) {
		if (activeEditor == null || !(activeEditor instanceof TextPage)) {
			CustomDialog.showErrorMessageBox("这个类不是" + TextPage.class.getName());
			return;
		}
		TextPage tp = (TextPage) activeEditor;
		Clipboard cb = new Clipboard(Display.getCurrent());
		String textData = tp.getEditorInput().getName();;
		TextTransfer textTransfer = TextTransfer.getInstance();
		cb.setContents(new Object[]{textData}, new Transfer[]{textTransfer});

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
