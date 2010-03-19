package uncertain.ide.eclipse.preferencePage;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.presentation.PreferenceLabelManager;


/**
 * A preference page for a simple HTML editor.
 */
public class SxsdDirectoryPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public SxsdDirectoryPreferencePage() {
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

		DirectoryFieldEditor sxsdDir = new DirectoryFieldEditor(
				PreferenceLabelManager.SXSD_DIRECTORY, "builtin-packages Ŀ¼", getFieldEditorParent());
		addField(sxsdDir);
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}
}
