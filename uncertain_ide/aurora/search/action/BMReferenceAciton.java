package aurora.search.action;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;

import aurora.search.reference.BMReferenceQuery;

public class BMReferenceAciton implements IObjectActionDelegate {
	private ISelection selection;
	private IWorkbenchPart targetPart;
	private IFile sourceFile;

	public BMReferenceAciton() {
	}

	public void run(IAction action) {
		IProject project = sourceFile.getProject();

		IContainer scope = findWebInf(sourceFile);
		if (scope == null) {
			scope = project;
		}else{
			scope = scope.getParent();
		}
		BMReferenceQuery query = new BMReferenceQuery(scope, sourceFile);
		NewSearchUI.runQueryInBackground(query);
	}

	private IContainer findWebInf(IResource resource) {

		if (null == resource) {
			return null;
		}
		String name = resource.getName();
		if (resource.getType() == IResource.FOLDER && "WEB-INF".equals(name)) {
			return (IContainer) resource;
		} else {
			return findWebInf(resource.getParent());
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		if (selection == null) {
			action.setEnabled(false);
			return;
		}
		List resources = IDE
				.computeSelectedResources((IStructuredSelection) selection);
		if (!resources.isEmpty()) {
			sourceFile = (IFile) resources.get(0);
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

}
