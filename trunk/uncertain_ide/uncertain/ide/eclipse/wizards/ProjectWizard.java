package uncertain.ide.eclipse.wizards;

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
		return true;
	}

	public void createPageControls(Composite pageContainer) {
		// super.createPageControls(pageContainer);
	}
}
