package uncertain.ide.eclipse.bm.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.ui.PartInitException;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.BaseCompositeMapEditor;
import uncertain.ide.eclipse.editor.CompositeMapPage;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CompositeMapLocatorParser;
import uncertain.ide.help.CustomDialog;
import aurora.ide.AuroraConstant;


public class BussinessModelEditor extends BaseCompositeMapEditor {

	protected BussinessModelPage mainFormPage ;
	private SQLExecutePage sqlPage  = new SQLExecutePage(this);
	int SQLPageIndex;
	public CompositeMapPage initMainViewerPage() {
		mainFormPage = new BussinessModelPage(this);
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
		int currentPage = getCurrentPage();
		super.pageChange(newPageIndex);
		if(newPageIndex ==SQLPageIndex){
			try {
				sqlPage.refresh(mainViewerPage.getFullContent());
			} catch (ApplicationException e) {
				CustomDialog.showErrorMessageBox(e);
			}
		}else if(currentPage==mainViewerIndex&&newPageIndex ==textPageIndex){
			locateTextPage();
		}else if(currentPage==textPageIndex&&newPageIndex ==mainViewerIndex){
			locateMainPage();
		}
	}

	private void locateMainPage() {
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();

		try {
			InputStream content = new ByteArrayInputStream(textPage.getContent()
					.getBytes("UTF-8"));
			CompositeMap  cm = parser.getCompositeMapFromLine(content, textPage.getCursorLine());
			if(cm != null){
				while(cm.getParent() != null){
					CompositeMap parent = cm.getParent();
					if(AuroraConstant.ModelQN.equals(parent.getQName())){
						mainFormPage.setSelectionTab(cm.getName());
					}
					cm = parent;
				}
			}
		} catch (Exception e){
			CustomDialog.showExceptionMessageBox(e);
		}
		
	}

	private void locateTextPage() {
		CompositeMap selection = mainFormPage.getSelectionTab();
		if(selection == null)
			return;
		CompositeMapLocatorParser parser = new CompositeMapLocatorParser();
		int line = 0;
		try {
			InputStream content = new ByteArrayInputStream(mainFormPage.getFullContent()
					.getBytes("UTF-8"));
			line = parser.LocateCompositeMapLine(content, selection);
		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
		int offset = textPage.getOffsetFromLine(line);
		textPage.setHighlightRange(offset, 10, true);
		
	}
}