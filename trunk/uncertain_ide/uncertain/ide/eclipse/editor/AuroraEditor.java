package uncertain.ide.eclipse.editor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.Activator;
import uncertain.ide.Common;


public abstract class AuroraEditor extends FormEditor {

	protected AuroraPage auroraPage ;
	private boolean dirty = false;
	private File file;

	public AuroraEditor() {
		super();
	}

	protected void addPages() {
		try {
			addPage(auroraPage);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		setSite(site);
		setInput(input);
		Activator.getWorkspace().addResourceChangeListener(
				new InputFileListener(this));
		IFile ifile = ((IFileEditorInput) input).getFile();
		file = new File(Common.getIfileLocalPath(ifile));
		String fileName = file.getName();
		// setPartName(fileName + " - " + getPartName());
		setPartName(fileName);
	
	}

	public void doSave(IProgressMonitor monitor) {
		setDirty(false);
		auroraPage.doSave(monitor);
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		super.editorDirtyStateChanged();
	}

	public boolean isDirty() {
		return dirty;
	}

	public File getFile() {
		return file;
	}

	public void makeDirty() {
		setDirty(true);
	}

	public void editorDirtyStateChanged() {
		setDirty(!this.dirty);
	}

}