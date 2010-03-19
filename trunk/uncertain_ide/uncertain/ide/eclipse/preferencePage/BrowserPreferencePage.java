package uncertain.ide.eclipse.preferencePage;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.presentation.PreferenceLabelManager;


/**
 * A preference page for a simple HTML editor.
 */
public class BrowserPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public BrowserPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		// Set the preference store for the preference page.
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	/**
	 * @see org.eclipse.jface.preference.
	 *      FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors() {

		StringFieldEditor  remoteUrl = new StringFieldEditor (
				PreferenceLabelManager.BROWSER_REMOTE, "远程服务器主页面", getFieldEditorParent());
		StringFieldEditor  localUrl = new StringFieldEditor (
				PreferenceLabelManager.BROWSER_LOCAL, "本地服务器主页面", getFieldEditorParent());
		
		
		addField(remoteUrl);
		addField(localUrl);
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}
}
