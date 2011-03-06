package uncertain.ide.eclipse.screen.editor;

import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.eclipse.editor.CompositeMapTreePage;
import uncertain.ide.help.LocaleMessage;

public class ServiceTreePage extends CompositeMapTreePage{
	protected static final String PageId = "ServicePage";
	protected static final String PageTitle = LocaleMessage.getString("screen.file");
	public ServiceTreePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
