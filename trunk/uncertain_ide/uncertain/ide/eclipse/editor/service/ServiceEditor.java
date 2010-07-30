package uncertain.ide.eclipse.editor.service;

import org.eclipse.ui.PartInitException;

import uncertain.ide.eclipse.editor.CompositeMapTreeEditor;
import uncertain.ide.eclipse.editor.CompositeMapTreePage;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;




public class ServiceEditor extends CompositeMapTreeEditor{



	public CompositeMapTreePage initTreePage() {
		ServiceTreePage treePage = new ServiceTreePage(this);
		return treePage;
	}
	
	protected void addPages() {
		BrowserPage be = new BrowserPage(this);
		try {
			super.addPages();
			addPage(be);
		} catch (PartInitException e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}

}