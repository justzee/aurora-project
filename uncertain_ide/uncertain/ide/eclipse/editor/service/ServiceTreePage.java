package uncertain.ide.eclipse.editor.service;

import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.eclipse.editor.CompositeMapTreePage;

public class ServiceTreePage extends CompositeMapTreePage{
	protected static final String PageId = "ServicePage";
	protected static final String PageTitle = "Service File";
	public ServiceTreePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
