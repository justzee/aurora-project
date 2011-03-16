package uncertain.ide.eclipse.navigator.action;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import uncertain.ide.AuroraProjectNature;
import uncertain.ide.help.CustomDialog;

public class RemoveAuroraNature implements IObjectActionDelegate {

	ISelection selection;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	public void run(IAction action) {
		if (!(selection instanceof IStructuredSelection)){
			CustomDialog.showErrorMessageBox(selection+"is not a IStructuredSelection!");
			return;
		}
		IStructuredSelection structured = (IStructuredSelection) selection;
		Object firstElment = structured.getFirstElement();
		if (!(firstElment instanceof IProject)){
			CustomDialog.showErrorMessageBox(firstElment+"is not a IProject!");
			return;
		}
		IProject project = (IProject) firstElment;
		if(!project.isOpen()){
			return;
		}
		try {
			if(AuroraProjectNature.hasAuroraNature(project)){
				AuroraProjectNature.removeAuroraNature(project);
			}
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;

	}
}
