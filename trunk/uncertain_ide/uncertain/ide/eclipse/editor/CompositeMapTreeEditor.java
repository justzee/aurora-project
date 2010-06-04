package uncertain.ide.eclipse.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.InputFileListener;
import uncertain.ide.eclipse.editor.textpage.TextPage;


public abstract class CompositeMapTreeEditor extends FormEditor {

	protected CompositeMapTreePage treePage ;
	protected TextPage textPage = new TextPage(this);
	private boolean dirty = false;
	private File file;

	public CompositeMapTreeEditor() {
		super();
		initTreePage();
	}
	protected abstract void initTreePage();

	protected void addPages() {
		try {
			addPage(treePage);
			addPage(textPage);
		} catch (PartInitException e) {
			Common.showExceptionMessageBox(null, e);
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
		if(!textPage.canLeaveThePage())
			return;
		setDirty(false);
		treePage.doSave(monitor);
//		textPage.doSave(monitor);
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
		if(!dirty)
			setDirty(true);
		if(dirty){
			if(textPage.isModify()){
				CompositeLoader loader = new CompositeLoader();
				try {
					treePage.refresh(loader.loadFromString(textPage.getOriginalContent(),"utf-8"));
				} catch (IOException e) {
					throw new RuntimeException(e.getLocalizedMessage());
				} catch (SAXException e) {
					throw new RuntimeException(e.getLocalizedMessage());
				}
				textPage.setModify(false);
			}else if(treePage.isModify()){
//				auroraPage.data
//				System.out.println("to xml:"+mainFormPage.getData().toXML());
				textPage.refresh(treePage.getData().toXML());
				treePage.setModify(false);
			}
		}
	}
	

}