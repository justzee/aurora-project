package uncertain.ide.eclipse.preferencepages;

import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import uncertain.ide.Activator;

public class AuroraTemplatePreferencePage extends TemplatePreferencePage
		implements IWorkbenchPreferencePage {

	public AuroraTemplatePreferencePage() {
		try {
			setPreferenceStore(Activator.getDefault().getPreferenceStore());
			setTemplateStore(AuroraTemplateManager.getInstance()
					.getTemplateStore());
			setContextTypeRegistry(AuroraTemplateManager.getInstance()
					.getContextTypeRegistry());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected boolean isShowFormatterSetting() {
		return false;
	}
}
