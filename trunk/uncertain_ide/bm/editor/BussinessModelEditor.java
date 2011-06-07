package bm.editor;

import helpers.ApplicationException;
import helpers.AuroraConstant;
import helpers.CompositeMapLocatorParser;
import helpers.CompositeMapUtil;
import helpers.DialogUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.ui.PartInitException;

import editor.BaseCompositeMapEditor;
import editor.CompositeMapPage;

import uncertain.composite.CompositeMap;


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
			DialogUtil.showExceptionMessageBox(e);
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
				DialogUtil.showExceptionMessageBox(e);
			}
		}else if(currentPage==mainViewerIndex&&newPageIndex ==textPageIndex){
			try {
				locateTextPage();
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
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
			DialogUtil.showExceptionMessageBox(e);
		}
		
	}

	private void locateTextPage() throws ApplicationException {
		CompositeMap selection = mainFormPage.getSelectionTab();
		if(selection == null)
			return;
		int line = 0;
		line = CompositeMapUtil.locateNode(mainFormPage.getFullContent(), selection);
		int offset = textPage.getOffsetFromLine(line);
		textPage.setHighlightRange(offset, 10, true);
		
	}
}