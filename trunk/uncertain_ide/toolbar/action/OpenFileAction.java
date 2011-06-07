/**
 * 
 */
package toolbar.action;

/**
 * @author linjinxiao
 *
 */
import helpers.DialogUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;


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
			DialogUtil.showExceptionMessageBox(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}
}
