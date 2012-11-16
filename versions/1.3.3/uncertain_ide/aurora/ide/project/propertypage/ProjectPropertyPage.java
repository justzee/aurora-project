package aurora.ide.project.propertypage;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.AuroraResourceUtil;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.ExceptionUtil;
import aurora.ide.helpers.LocaleMessage;
import aurora.ide.helpers.ProjectUtil;
import aurora.ide.helpers.SystemException;

public class ProjectPropertyPage extends PropertyPage {
	public ProjectPropertyPage() {
	}

	public static final String PropertyId = "aurora.ide.projectproperty";
	private static final String LOCAL_WEB_URL = "LOCAL_WEB_URL";
	private static final String WEB_HOME = "WEB_HOME";
	private static final String BM_HOME = "BM_HOME";
	private static final String DebugMode = "DEBUG_MODE";
	private static final String BUILD_RIGHT_NOW = "BUILD_RIGHT_NOW";
	public static final QualifiedName LoclaUrlHomeQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, LOCAL_WEB_URL);
	public static final QualifiedName WebQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, WEB_HOME);
	public static final QualifiedName BMQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, BM_HOME);
	public static final QualifiedName buildNow = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, BUILD_RIGHT_NOW);
	public static final QualifiedName DebugModeQN = new QualifiedName(
			AuroraPlugin.PLUGIN_ID, DebugMode);
	private Text localWebUrlText;
	private Text webHomeText;
	private Text bmHomeText;
//	private Button debugButton;
	private Button cb_isBuild;

	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		content.setLayoutData(gridData);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		content.setLayout(layout);

		// web url
		Label localWebLabel = new Label(content, SWT.NONE);
		localWebLabel.setText("预览主页面");
		localWebUrlText = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		localWebUrlText.setLayoutData(gridData);
		try {
			String localWebUrl = getProject().getPersistentProperty(
					LoclaUrlHomeQN);
			if (filtEmpty(localWebUrl) != null) {
				localWebUrlText.setText(localWebUrl);
			} else {
				localWebUrl = ProjectUtil.autoGetLocalWebUrl(getProject());
				if (filtEmpty(localWebUrl) != null) {
					localWebUrlText.setText(localWebUrl);
				}
			}
		} catch (Throwable e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		// webDir
		Label webDirGroup = new Label(content, SWT.NONE);
		webDirGroup.setText("Web主目录");
		webHomeText = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		webHomeText.setLayoutData(gridData);
		String webDir = null;
		try {
			webDir = getProject().getPersistentProperty(WebQN);
			if (filtEmpty(webDir) != null) {
				webHomeText.setText(webDir);
			} else {
				webDir = ProjectUtil.autoGetWebHome(getProject());
				if (filtEmpty(webDir) != null) {
					webHomeText.setText(webDir);
				}
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		} catch (SystemException e) {
			DialogUtil.logErrorException(e);
		}

		Button webBrowseButton = new Button(content, SWT.PUSH);
		webBrowseButton.setText("浏览(&W)..");
		webBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(
						Display.getCurrent().getActiveShell(), getProject(),
						false, LocaleMessage
								.getString("please.select.the.path"));
				if (dialog.open() == ContainerSelectionDialog.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 1) {
						IPath selectionPath = (IPath) result[0];
						String errorMessage = validWebHome(getProject(),
								selectionPath);
						if (errorMessage != null) {
							if (DialogUtil.showConfirmDialogBox("校验不通过："
									+ errorMessage
									+ AuroraResourceUtil.LineSeparator
									+ "是否仍然继续设置?") != SWT.OK) {
								return;
							}
						}
						webHomeText.setText(selectionPath.toString());
					}
				}
			}
		});
		// BMDir
		Label bmDirLabel = new Label(content, SWT.NONE);
		bmDirLabel.setText("BM主目录");
		bmHomeText = new Text(content, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		bmHomeText.setLayoutData(gridData);
		String bmDir = null;
		try {
			bmDir = getProject().getPersistentProperty(BMQN);
			if (filtEmpty(bmDir) != null) {
				bmHomeText.setText(bmDir);
			} else {
				bmDir = ProjectUtil.autoGetBMHome(getProject());
				if (filtEmpty(bmDir) != null) {
					bmHomeText.setText(bmDir);
				}
			}
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		} catch (SystemException e) {
			DialogUtil.logErrorException(e);
		}

		Button bmBrowseButton = new Button(content, SWT.PUSH);
		bmBrowseButton.setText(LocaleMessage.getString("openBrowse"));
		bmBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(
						Display.getCurrent().getActiveShell(), getProject(),
						false, LocaleMessage
								.getString("please.select.the.path"));
				if (dialog.open() == ContainerSelectionDialog.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 1) {
						IPath selectionPath = (IPath) result[0];
						String errorMessage = validBMHome(getProject(),
								selectionPath);
						if (errorMessage != null) {
							if (DialogUtil.showConfirmDialogBox("校验不通过："
									+ errorMessage
									+ AuroraResourceUtil.LineSeparator
									+ "是否仍然继续设置?") != SWT.OK) {
								return;
							}
						}
						bmHomeText.setText(selectionPath.toString());
					}
				}
			}
		});
		Button testConn = new Button(content, SWT.PUSH);
		testConn.setText("保存设置,并测试数据库链接");
		gridData = new GridData(GridData.BEGINNING);
		gridData.horizontalSpan = 3;
		testConn.setLayoutData(gridData);
		testConn.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (checkInput()) {
					String errorMessage = validWebHome(getProject(), new Path(
							webHomeText.getText()));
					if (errorMessage != null) {
						if (DialogUtil.showConfirmDialogBox("校验不通过："
								+ errorMessage
								+ AuroraResourceUtil.LineSeparator
								+ "是否仍然继续设置?") != SWT.OK) {
							return;
						}
					} else {
						DialogUtil.showMessageBox(SWT.ICON_INFORMATION, "OK",
								"数据库连接测试成功!");
					}
				}

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

//		debugButton = new Button(content, SWT.CHECK);
//		debugButton.setText("调试模式 ( 记录IDE运行日志,不建议开启. )");
//		String debugMode = null;
//		try {
//			debugMode = getProject().getPersistentProperty(DebugModeQN);
//		} catch (CoreException e) {
//			DialogUtil.showExceptionMessageBox(e);
//		}
//		if ("true".equals(debugMode)) {
//			debugButton.setSelection(true);
//		}
//		gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.horizontalSpan = 3;
//		debugButton.setLayoutData(gridData);

		cb_isBuild = new Button(content, SWT.CHECK);
		cb_isBuild.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 2, 1));
		cb_isBuild.setText("立即开始 build ( build 过程可能耗时较长 )");

		cb_isBuild.setSelection(getStoredBuildOption());
		new Label(content, SWT.NONE);
		return content;
	}

	private boolean checkInput() {
		IProject project = getProject();
		if (webHomeText.getText() == null || "".equals(webHomeText.getText())) {
			setErrorMessage("请先设置Web主目录.");
			return false;
		}
		if (webHomeText.getText() != null) {
			try {
				project.setPersistentProperty(WebQN, webHomeText.getText());
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
			}
		}
		if (bmHomeText.getText() != null) {
			try {
				project.setPersistentProperty(BMQN, bmHomeText.getText());
			} catch (CoreException e) {
				DialogUtil.logErrorException(e);
			}
		}
		try {
//			project.setPersistentProperty(DebugModeQN,
//					String.valueOf(debugButton.getSelection()));
			project.setPersistentProperty(LoclaUrlHomeQN,
					localWebUrlText.getText());
			project.setPersistentProperty(buildNow,
					cb_isBuild.getSelection() ? "true" : "false");
		} catch (CoreException e) {
			DialogUtil.logErrorException(e);
		}
		return true;
	}

	public boolean performOk() {
		if (checkInput() && cb_isBuild.getSelection()) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					try {
						AuroraPlugin.getDefault().getWorkbench()
								.getProgressService()
								.busyCursorWhile(new IRunnableWithProgress() {
									public void run(IProgressMonitor monitor)
											throws InvocationTargetException,
											InterruptedException {
										try {
											getProject()
													.build(IncrementalProjectBuilder.FULL_BUILD,
															monitor);
										} catch (CoreException e) {
											e.printStackTrace();
										}
									}
								});
					} catch (InvocationTargetException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return true;
	}

	private IProject getProject() {
		return (IProject) getElement();
	}

	public static String filtEmpty(String str) {
		if ("".equals(str))
			return null;
		return str;
	}

	public static String validWebHome(IProject project, IPath path) {
		IResource selectionResource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);
		if (selectionResource == null) {
			return "此目录不存在，请重新选择！";
		}
		if (!project.equals(selectionResource.getProject())) {
			return "请选择本工程内的目录";
		}
		String locationPath = selectionResource.getLocation().toOSString();
		if (locationPath == null) {
			return "文件系统中不存在此目录!";
		}
		try {
			DBConnectionUtil.testDBConnection(project, locationPath);
		} catch (ApplicationException e) {
			return ExceptionUtil.getExceptionTraceMessage(e);
		}
		return null;
	}

	public static String validBMHome(IProject project, IPath path) {
		String classesDir = "classes";
		IResource selectionResource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);
		if (selectionResource == null) {
			return "此目录不存在，请重新选择！";
		}
		if (!project.equals(selectionResource.getProject())) {
			return "请选择本工程内的目录";
		}
		String locationPath = selectionResource.getLocation().toOSString();
		if (locationPath == null) {
			return "文件系统中不存在此目录!";
		}
		if (!classesDir.equals(selectionResource.getName().toLowerCase())) {
			return "此文件名不是" + classesDir;
		}
		return null;
	}

	private boolean getStoredBuildOption() {
		String str = null;
		try {
			str = getProject().getPersistentProperty(buildNow);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (str == null)
			return false;
		return str.endsWith("true");
	}
}
