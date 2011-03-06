package uncertain.ide.eclipse.editor;

import java.io.IOException;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeMap;
import uncertain.ide.eclipse.editor.core.IViewer;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LoadSchemaManager;
import uncertain.schema.SchemaManager;

public class CompositeMapTreeShell implements IViewer {

	protected IViewer mParentViewer;
	BaseCompositeMapViewer baseCompositeMapPage;
	private CompositeMap data;
	private Shell shell;

	public CompositeMapTreeShell(IViewer parent, CompositeMap data) {
		mParentViewer = parent;
		this.data = data;
	}

	public void createFormContent(Shell shell) {

		try {
			this.shell = shell;
			createContent(shell);

		} catch (Exception e) {
			CustomDialog.showExceptionMessageBox(e);
		}
	}

	protected void createContent(Composite shell) throws ApplicationException {

		baseCompositeMapPage = new BaseCompositeMapViewer(this, data);
		baseCompositeMapPage.createFormContent(shell);
	}

	public void refresh(boolean dirty) {
		String text = shell.getText();
		if (text.indexOf("*") == -1)
			text = "*" + text;
		shell.setText(text);
		if (dirty) {
			mParentViewer.refresh(true);
		}
		baseCompositeMapPage.refresh(false);

	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		SchemaManager sm = LoadSchemaManager.getSchemaManager();
		try {
			sm.loadSchemaFromClassPath("aurora.testcase.ui.config.components",
					"sxsd");

			sm.loadSchemaFromClassPath("aurora.testcase.ui.config.service",
					"sxsd");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
	}
}