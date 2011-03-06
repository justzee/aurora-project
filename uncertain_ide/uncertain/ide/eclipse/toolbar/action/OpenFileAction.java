/**
 * 
 */
package uncertain.ide.eclipse.toolbar.action;

/**
 * @author linjinxiao
 *
 */
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import uncertain.ide.help.CustomDialog;

public class OpenFileAction implements IWorkbenchWindowActionDelegate {
	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		IHandlerService hs = (IHandlerService) PlatformUI.getWorkbench().getAdapter(IHandlerService.class);
		try {
			hs.executeCommand("org.eclipse.ui.navigate.openResource", null);
		}catch(Exception e){
			CustomDialog.showErrorMessageBox(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
