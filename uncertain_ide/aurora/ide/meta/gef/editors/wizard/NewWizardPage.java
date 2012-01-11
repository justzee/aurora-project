package aurora.ide.meta.gef.editors.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import aurora.ide.helpers.LocaleMessage;
import aurora.ide.meta.gef.extension.ExtensionManager;

public class NewWizardPage extends WizardPage {

	private Text txtPath;
	private Text txtFile;
	private boolean isComplete;

	protected NewWizardPage() {
		super("aurora.wizard.new.Page");
		setTitle("新建");
		setDescription("新建文件");
		setPageComplete(false);
	}

	public String getTxtPath() {
		return txtPath.getText().trim();
	}

	public String getTxtFile() {
		String fileName = txtFile.getText().trim();
		if (fileName.length() > 0 && fileName.indexOf(".") == -1) {
			fileName = fileName + ".meta";
		}
		return fileName;
	}

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		setControl(composite);
		composite.setLayout(new GridLayout(3, false));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		Label lblPath = new Label(composite, SWT.NONE);
		lblPath.setText("目录");
		txtPath = new Text(composite, SWT.BORDER);
		txtPath.setLayoutData(gd);
		Button btnBrower = new Button(composite, SWT.NONE);
		btnBrower.setText("浏览...");

		gd = new GridData(GridData.FILL_HORIZONTAL);
		Label lblFile = new Label(composite, SWT.NONE);
		lblFile.setText("文件名");
		txtFile = new Text(composite, SWT.BORDER);
		txtFile.setLayoutData(gd);

		txtPath.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		txtFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		btnBrower.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot().getProject(), true, "");
				dialog.setHelpAvailable(true);
				dialog.setTitle("选择目录");
				if (dialog.open() == Dialog.OK) {
					if (dialog.getResult().length != 0) {
						txtPath.setText(dialog.getResult()[0].toString());
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void updateStatus(String message) {
		setErrorMessage(message);
		isComplete = (message == null);
		if (!hasNextPage()) {
			setPageComplete(false);
		} else {
			setPageComplete(isComplete);
		}
	}

	public boolean isComplete() {
		return isComplete;
	}

	public boolean hasNextPage() {
		if (ExtensionManager.getInstance().getBeans().size() > 0) {
			return true;
		}
		return false;
	}

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getTxtPath()));
		String fileName = getTxtFile();

		if (getTxtPath().length() == 0) {
			updateStatus(LocaleMessage.getString("file.container.must.be.specified"));
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(LocaleMessage.getString("file.container.must.exist"));
			return;
		}
		if (fileName != null && !fileName.equals("") && ((IContainer) container).getFile(new Path(fileName)).exists()) {
			updateStatus(LocaleMessage.getString("filename.used"));
			return;
		}
		if (!container.isAccessible()) {
			updateStatus(LocaleMessage.getString("project.must.be.writable"));
			return;
		}
		if (fileName.length() == 0) {
			updateStatus(LocaleMessage.getString("file.name.must.be.specified"));
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus(LocaleMessage.getString("file.name.must.be.valid"));
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("meta") == false) {
				updateStatus("文件扩展名必须是meta");
				return;
			}
		}
		updateStatus(null);
	}
}
