package project.wizard;

import ide.AuroraProjectNature;
import helpers.DialogUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;


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
			DialogUtil.showExceptionMessageBox(e);
		}
		return true;
	}

	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer);
	}
}
