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
import uncertain.composite.CompositeMap;
import uncertain.ide.Activator;
import uncertain.ide.Common;
import uncertain.ide.eclipse.action.InputFileListener;
import uncertain.ide.eclipse.editor.textpage.TextPage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;


public abstract class BaseCompositeMapEditor extends FormEditor {

	protected CompositeMapPage mainViewerPage ;
	protected TextPage textPage = new TextPage(this);
	private boolean dirty = false;
	private File file;
	int mainViewerIndex ;
	int textPageIndex;
	public BaseCompositeMapEditor() {
		super();
		this.mainViewerPage = initMainViewerPage();
	}
	public abstract CompositeMapPage initMainViewerPage();
	
	protected void addPages() {
		try {
			mainViewerIndex = addPage(mainViewerPage);
			textPageIndex = addPage(textPage,getEditorInput());
			setPageText(textPageIndex, TextPage.textPageTitle);
		} catch (PartInitException e) {
			CustomDialog.showExceptionMessageBox(e);
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
		setPartName(fileName);
	
	}

	public void doSave(IProgressMonitor monitor) {
		int currentPage = getCurrentPage();
		if(currentPage == textPageIndex){
			try {
				sycMainViewerPageWithTextPage();
			} catch (Exception e) {
				CustomDialog.showExceptionMessageBox(e);
			}
		}
		mainViewerPage.doSave(monitor);
		setDirty(false);
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
	public void editorDirtyStateChanged() {
		if(!dirty)
			setDirty(true);
	}
	
	protected void pageChange(int newPageIndex) {
		int currentPage = getCurrentPage();
		if(currentPage==textPageIndex&&newPageIndex ==mainViewerIndex){
			try {
				sycMainViewerPageWithTextPage();
			} catch (Exception e) {
				setActivePage(textPageIndex);
				CustomDialog.showExceptionMessageBox(e);
			}
		}
		super.pageChange(newPageIndex);
		if(currentPage==mainViewerIndex&&newPageIndex ==textPageIndex){
			sycTextPageWithMainViewerPage();
		} 
	}
	private boolean sycMainViewerPageWithTextPage() throws IOException, SAXException {
		CompositeLoader loader = new CompositeLoader();
		CompositeMap cm = loader.loadFromString(textPage.getContent(),"UTF-8");
		mainViewerPage.setContent(cm);
		return true;
	}
	private boolean sycTextPageWithMainViewerPage(){
		textPage.refresh(mainViewerPage.getFullContent());
		return true;
	}

}