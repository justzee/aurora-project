package uncertain.ide.eclipse.editor.widgets.config;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import uncertain.ide.eclipse.editor.widgets.core.IUpdateMessageDialog;
import uncertain.ide.util.LocaleMessage;

/**
 * @author linjinxiao
 * 
 */
public class ProjectDirPicker {

	protected IUpdateMessageDialog dialog;
	protected Text dirText;
	protected Button dirButton;
	protected String dirStr;
	private String localePath;

	/**
	 * @param dialog
	 */
	public ProjectDirPicker(IUpdateMessageDialog dialog) {
		this.dialog = dialog;
	}

	public ProjectDirPicker() {
	}

	/**
	 * @param dialog
	 */
	public void setUpdateMessageDialog(IUpdateMessageDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * @param parent
	 * @return
	 */
	public Composite createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gridData);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		composite.setLayout(gl);

		Group fileGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		fileGroup.setLayout(layout);
		fileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileGroup.setText(LocaleMessage.getString("path"));

		dirText = new Text(fileGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		dirText.setLayoutData(data);
		dirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				valid();
			}
		});
		dirText.setEditable(false);

		dirButton = new Button(fileGroup, SWT.PUSH);
		dirButton.setData(data);
		dirButton.setText(LocaleMessage.getString("openBrowse"));
		dirButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleBrowse();
			}
		});
		return composite;

	}

	void handleLocationBrowseButton() {
		DirectoryDialog dialog = new DirectoryDialog(new Shell(), SWT.NONE);
		dialog.setMessage(LocaleMessage.getString("please.select.the.path"));
		dialog.setText(LocaleMessage.getString("dialog"));
		String result = dialog.open();
		if (result != null) {
			dirText.setText(result);
		}
	}

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				new Shell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				LocaleMessage.getString("please.select.the.path"));
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				Path path = (Path) result[0];
				String pathStr = path.toOSString() + File.separator;
				localePath = ProjectProperties.getProjectFileLocalPath(pathStr);
				dirText.setText(path.toString());
			}
		}
	}

	protected void valid() {
		if (dirText.getText() == null) {
			updateStatus(LocaleMessage.getString("path.must.be.specified"));
			return;
		}
		updateStatus(null);
		dirStr = dirText.getText();
	}

	/**
	 * @param message
	 */
	protected void updateStatus(String message) {
		dialog.updateStatus(message);
	}

	public String getDirPath() {
		return dirStr;
	}

	protected String getFullPathOfDir() {
		if (dirText.getText() == null || dirText.getText().equals(""))
			return null;
		return localePath;
	}
}
