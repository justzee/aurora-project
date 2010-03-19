package uncertain.ide.eclipse.editor.service;

import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.eclipse.editor.MainFormPage;

public class ServicePage extends MainFormPage{
	protected static final String PageId = "ServicePage";
	protected static final String PageTitle = "Service File";
	public ServicePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
