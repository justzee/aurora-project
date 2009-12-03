package uncertain.ide.eclipse.editor.sxsd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import uncertain.composite.CompositeLoader;
import uncertain.ide.eclipse.editor.MainFormPage;
import uncertain.ide.eclipse.editor.TreeEditor;

public class SxsdPage extends  MainFormPage{
	private static final String PageId = "SxsdTreePage";
	private static final String PageTitle = "Simple XML Schema";
//	public static String namespacePrefix;
//	public static String namespaceUrl;

	public SxsdPage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
