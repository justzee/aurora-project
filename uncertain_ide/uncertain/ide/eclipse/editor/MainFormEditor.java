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


public abstract class MainFormEditor extends FormEditor {

	protected MainFormPage mainFormPage ;
	private TextPage textPage = new TextPage(this);
	private boolean dirty = false;
	private File file;

	public MainFormEditor() {
		super();
	}

	protected void addPages() {
		try {
			addPage(mainFormPage);
			addPage(textPage);
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
		mainFormPage.doSave(monitor);
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
					
					mainFormPage.refresh(loader.loadFromString(textPage.getOriginalContent()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textPage.setModify(false);
			}else if(mainFormPage.isModify()){
//				auroraPage.data
				textPage.refresh(mainFormPage.getData().toXML());
				mainFormPage.setModify(false);
			}
		}
	}
	

}