package uncertain.ide.eclipse.preferencepages;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.SxsdValidVisablePropertyTester;


/**
 * A preference page for a simple HTML editor.
 */
public class SxsdDirectoryPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	public static final String SXSD_DIRECTORY = "sxsdDirectory";
	DirectoryFieldEditor sxsdDirEditor;
	
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

		sxsdDirEditor = new DirectoryFieldEditor(
				SXSD_DIRECTORY, Common.getString("builtin-packages.dir"), getFieldEditorParent());
		addField(sxsdDirEditor);
	}

	/**
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
	}
	
	public boolean performOk(){
		String sxsdDir = sxsdDirEditor.getStringValue();
		boolean isValid = SxsdValidVisablePropertyTester.isValidDir(new File(sxsdDir));
		if(!isValid){
			Common.showErrorMessageBox(null, "This path is not valid sxsd dir!");
			return false;
		}
		super.performOk();
		Common.refeshSchemaManager();
		return true;
	}
}
