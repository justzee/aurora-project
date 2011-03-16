package uncertain.ide.eclipse.project.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import uncertain.ide.AuroraProjectNature;
import uncertain.ide.help.CustomDialog;

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
			CustomDialog.showErrorMessageBox(e);
		}
		return true;
	}

	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer);
	}
}
