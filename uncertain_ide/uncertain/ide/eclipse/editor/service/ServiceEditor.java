package uncertain.ide.eclipse.editor.service;

import org.eclipse.ui.PartInitException;

import uncertain.ide.Common;
import uncertain.ide.eclipse.editor.CompositeMapTreeEditor;




public class ServiceEditor extends CompositeMapTreeEditor{

	public void initTreePage(){
		treePage = new ServiceTreePage(this);
	}
	
	protected void addPages() {
		BrowserPage be = new BrowserPage(this);
		try {
			addPage(treePage);
			addPage(textPage);
			addPage(be);
		} catch (PartInitException e) {
			Common.showExceptionMessageBox(null, e);
		}
	}
}