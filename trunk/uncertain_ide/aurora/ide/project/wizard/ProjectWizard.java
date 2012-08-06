package aurora.ide.project.wizard;


import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import aurora.ide.AuroraProjectNature;
import aurora.ide.helpers.DialogUtil;


public class ProjectWizard extends BasicNewProjectResourceWizard {

	public static final String copyright = "(c) Copyright HAND Enterprise Solutions Company Ltd.";

	public void addPages() {
		super.addPages();
	}

	public boolean performFinish() {
		if (!super.performFinish())
			return false;
		try {
			AuroraProjectNature.addAuroraNature(getNewProject());
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
		return true;
	}

	public void createPageControls(Composite pageContainer) {
		 super.createPageControls(pageContainer);
	}
}
