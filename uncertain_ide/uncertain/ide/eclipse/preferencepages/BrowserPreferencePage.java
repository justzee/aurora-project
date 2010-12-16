package uncertain.ide.eclipse.preferencepages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uncertain.ide.Activator;
import uncertain.ide.util.LocaleMessage;


/**
 * A preference page for a simple HTML editor.
 */
public class BrowserPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public static final String BROWSER_REMOTE = "remoteUrl";
	public static final String BROWSER_LOCAL = "localUrl";
	
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
				BROWSER_REMOTE, LocaleMessage.getString("remote.server.page"), getFieldEditorParent());
		StringFieldEditor  localUrl = new StringFieldEditor (
				BROWSER_LOCAL, LocaleMessage.getString("local.server.page"), getFieldEditorParent());
		
		
		addField(remoteUrl);
		addField(localUrl);
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}
}
