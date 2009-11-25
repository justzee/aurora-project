package uncertain.ide.eclipse.preferencePage;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

import uncertain.ide.Activator;
import uncertain.ide.Common;


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
				Common.SXSD_DIRECTORY, "Sxsd Ŀ¼", getFieldEditorParent());
		addField(sxsdDir);
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}
}
