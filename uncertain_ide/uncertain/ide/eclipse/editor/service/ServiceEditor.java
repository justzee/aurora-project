package uncertain.ide.eclipse.editor.service;

import uncertain.ide.eclipse.editor.MainFormEditor;




public class ServiceEditor extends MainFormEditor{

	public ServiceEditor() {
		super();
		mainFormPage = new ServicePage(this);
	}
}