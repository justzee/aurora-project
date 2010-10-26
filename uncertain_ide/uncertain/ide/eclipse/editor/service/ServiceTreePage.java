package uncertain.ide.eclipse.editor.service;

import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.LocaleMessage;
import uncertain.ide.eclipse.editor.CompositeMapTreePage;

public class ServiceTreePage extends CompositeMapTreePage{
	protected static final String PageId = "ServicePage";
	protected static final String PageTitle = LocaleMessage.getString("screen.file");
	public ServiceTreePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
