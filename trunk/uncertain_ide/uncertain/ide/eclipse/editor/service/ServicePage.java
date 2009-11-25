package uncertain.ide.eclipse.editor.service;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import uncertain.ide.eclipse.editor.AuroraPage;
import uncertain.ide.eclipse.editor.AuroraTreeEditor;

public class ServicePage extends AuroraPage{
	protected static final String PageId = "ServicePage";
	protected static final String PageTitle = "Service File";
	public ServicePage(FormEditor editor) {
		super(editor, PageId, PageTitle);
	}
}
