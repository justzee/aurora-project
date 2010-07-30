package uncertain.ide.eclipse.editor;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uncertain.composite.CompositeMap;
import uncertain.ide.LoadSchemaManager;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.schema.SchemaManager;

public class CompositeMapTreeShell implements IViewer {

	protected IViewer mParentViewer;
	BaseCompositeMapViewer baseCompositeMapPage;
	private CompositeMap data;
	private Shell shell;
	public CompositeMapTreeShell(IViewer parent, CompositeMap data){
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

	protected void createContent(Composite shell) {

		baseCompositeMapPage = new BaseCompositeMapViewer(this,data);
		baseCompositeMapPage.createFormContent(shell);
	}

	public void refresh(boolean dirty) {
		String text = shell.getText();
		if(text.indexOf("*")==-1)
			text = "*" + text;
		shell.setText(text);
		if (dirty) {
			mParentViewer.refresh(true);
		}
		baseCompositeMapPage.refresh(false);

	}
	public static void main(String[] args) throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
	
		SchemaManager sm = LoadSchemaManager.getSchemaManager();
		sm.loadSchemaFromClassPath("aurora.testcase.ui.config.components",
				"sxsd");
		sm.loadSchemaFromClassPath("aurora.testcase.ui.config.service", "sxsd");
		shell.open();
	
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	
		display.dispose();
	}
}