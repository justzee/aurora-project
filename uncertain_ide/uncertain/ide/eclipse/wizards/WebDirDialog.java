package uncertain.ide.eclipse.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import uncertain.ide.LocaleMessage;

public class WebDirDialog extends TitleAreaDialog implements UpdateMessageDialog{
	private ProjectDirPicker dirPicker;
	private String title;
	
	private WebDirDialog(Shell shell,String title,ProjectDirPicker dirPicker) {
		super(shell);
		this.title = title;
		this.dirPicker = dirPicker;
		dirPicker.setUpdateMessageDialog(this);
	}
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle(title);
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
		Composite composite =  dirPicker.createControl(control);
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

	public String getDirPath(){
		return dirPicker.getDirPath();
	}
	public void updateStatus(String message) {
		setErrorMessage(message);
		Button OK = getButton(IDialogConstants.OK_ID);
		if (message == null) {
			OK.setEnabled(true);
		}
	}
	public static String getWebBaseDir(IProject project) throws Exception{
		WebDirDialog dir = new WebDirDialog(new Shell(),LocaleMessage.getString("select.aurora.web.application.path"),new WebDirPicker());
		if (dir.open() == Window.OK) {
			String path = dir.getDirPath();
			if (path != null && path.length() > 0){
				return path;
			}
		}
		return null;
	}
	public static String getBMBaseDir(IProject project) throws Exception{
		WebDirDialog dir = new WebDirDialog(new Shell(),LocaleMessage.getString("select.bussiness.model.directory"),new ProjectDirPicker());
		if (dir.open() == Window.OK) {
			String path = dir.getDirPath();
			if (path != null && path.length() > 0){
				return path;
			}
		}
		return null;
	}
}
