package uncertain.ide.eclipse.editor.sxsd;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.eclipse.editor.InputFileListener;

import aurora_ide.Activator;
import aurora_ide.Common;

public class SxsdEditor extends FormEditor {

	private SxsdPage sxsdTreePage = new SxsdPage(this);
	private boolean dirty = false;
	private File file;

	protected void addPages() {
		try {
			addPage(sxsdTreePage);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
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
		sxsdTreePage.doSave(monitor);
	}

	public void doSaveAs() {
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	void setDirty(boolean dirty) {
		this.dirty = dirty;
		editorDirtyStateChanged();
	}

	public boolean isDirty() {
		return dirty;
	}

	public File getFile() {
		return file;
	}
}