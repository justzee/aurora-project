package uncertain.ide.eclipse.editor.widgets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;

public abstract class WizardPageRefreshable extends WizardPage {

	protected WizardPageRefreshable(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	protected WizardPageRefreshable(String pageName) {
		super(pageName);
	}
		
	public void refresh() {
	}

}
