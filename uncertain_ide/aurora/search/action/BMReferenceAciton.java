package aurora.search.action;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;

import aurora.search.core.Util;
import aurora.search.reference.BMReferenceQuery;

public class BMReferenceAciton implements IObjectActionDelegate {
	private IFile sourceFile;

	public BMReferenceAciton() {
	}

	public void run(IAction action) {
		IProject project = sourceFile.getProject();

		IContainer scope = Util.findWebInf(sourceFile);
		if (scope == null) {
			scope = project;
		} else {
			scope = scope.getParent();
		}
		BMReferenceQuery query = new BMReferenceQuery(scope, sourceFile);
		NewSearchUI.runQueryInBackground(query);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		boolean isEnable = checkSelection(selection);
		action.setEnabled(isEnable);

	}

	private boolean checkSelection(ISelection selection) {
		if (selection == null) {
			return false;
		}
		List resources = IDE
				.computeSelectedResources((IStructuredSelection) selection);
		if (resources.isEmpty()) {
			return false;
		}
		Object resource = resources.get(0);
		if (!(resource instanceof IFile)) {
			return false;
		}
		sourceFile = (IFile) resource;
		return true;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
