package uncertain.ide.eclipse.editor.bm;

import org.eclipse.ui.PartInitException;

import uncertain.ide.eclipse.editor.BaseCompositeMapEditor;
import uncertain.ide.eclipse.editor.CompositeMapPage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;


public class BussinessModelEditor extends BaseCompositeMapEditor {

	protected BussinessModelPage mainFormPage ;
	private SQLExecutePage sqlPage  = new SQLExecutePage(this);
	int SQLPageIndex;
	public CompositeMapPage initMainViewerPage() {
		BussinessModelPage mainFormPage = new BussinessModelPage(this);
		return mainFormPage;
	}
	
	protected void addPages() {
		try {
			super.addPages();
			SQLPageIndex = addPage(sqlPage);
		} catch (PartInitException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}

	public void editorDirtyStateChanged() {
		super.editorDirtyStateChanged();
		sqlPage.setModify(true);
	}
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if(newPageIndex ==SQLPageIndex){
			try {
				sqlPage.refresh(mainViewerPage.getFullContent());
			} catch (Exception e) {
				CustomDialog.showExceptionMessageBox(e);
			}
		}
	}
}