package uncertain.ide.eclipse.editor.sxsd;

import org.eclipse.ui.forms.editor.FormEditor;

import uncertain.ide.eclipse.editor.MainFormPage;

public class SxsdPage extends  MainFormPage{
	private static final String PageId = "SxsdTreePage";
	private static final String PageTitle = "Simple XML Schema";
//	public static String namespacePrefix;
//	public static String namespaceUrl;

	public SxsdPage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
