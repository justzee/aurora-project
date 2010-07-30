package uncertain.ide.eclipse.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import uncertain.ide.eclipse.editor.bm.UncertainProject;

public class UncertainWebAppPathDialog extends TitleAreaDialog implements UpdateMessageDialog{
	private UncertainWebAppPathCompoment webPath;
	public UncertainWebAppPathDialog(Shell shell) {
		super(shell);
	}
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Set Uncertain Web Application Path");
		return contents;
	}

	/**
	 * Creates the gray area
	 * 
	 * @param parent
	 *            the parent composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);

//		Composite composite = new Composite(control, SWT.NONE);
		webPath = new UncertainWebAppPathCompoment(this);
		Composite composite =  webPath.createControl(control);
		return composite;
	}
	/**
	 * Creates the buttons for the button bar
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		Button OK = getButton(IDialogConstants.OK_ID);
		OK.setEnabled(false);
	}

	protected boolean isResizable() {
		return true;
	}

	public String getUncertainProDir(){
		return webPath.getUncertainProDir();
	}
	public void updateStatus(String message) {
		setErrorMessage(message);
		Button OK = getButton(IDialogConstants.OK_ID);
		if (message == null) {
			OK.setEnabled(true);
		}
	}
	public static boolean  createWebAppDialog(IProject project) throws Exception{
		UncertainWebAppPathDialog dir = new UncertainWebAppPathDialog(
		new Shell());
		if (dir.open() == Window.OK) {
			String path = dir.getUncertainProDir();
			if (path != null && path.length() > 0){
				UncertainProject.createProjectFile(project,path);
				return true;
			}
		}
		return false;
	}
}
