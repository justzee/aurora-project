package uncertain.ide.eclipse.editor.service;

import org.eclipse.ui.PartInitException;

import uncertain.ide.eclipse.editor.BrowserPage;
import uncertain.ide.eclipse.editor.MainFormEditor;




public class ServiceEditor extends MainFormEditor{
	BrowserPage be;
	public ServiceEditor() {
		super();
		mainFormPage = new ServicePage(this);
		be = new BrowserPage(this);
	}
	protected void addPages() {
		try {
			addPage(mainFormPage);
			addPage(textPage);
			addPage(be);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}