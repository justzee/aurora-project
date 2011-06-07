package project.propertypage;

import helpers.ApplicationException;
import helpers.AuroraConstant;
import helpers.AuroraResourceUtil;
import helpers.DBConnectionUtil;
import helpers.DialogUtil;
import helpers.ExceptionUtil;
import helpers.LocaleMessage;
import helpers.ProjectUtil;
import helpers.SystemException;
import ide.AuroraPlugin;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.xml.sax.SAXException;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;

public class ProjectPropertyPage extends PropertyPage {
	public static final String PropertyId = "aurora.ide.projectproperty";
	private static final String LOCAL_WEB_URL = "LOCAL_WEB_URL";
	private static final String WEB_HOME = "WEB_HOME";
	private static final String BM_HOME = "BM_HOME";
	private static final String LOG_HOME = "LOG_HOME";
	private static final String DebugMode = "DEBUG_MODE";
	public static final QualifiedName LoclaUrlHomeQN = new QualifiedName(AuroraPlugin.PLUGIN_ID, LOCAL_WEB_URL);
	public static final QualifiedName WebQN = new QualifiedName(AuroraPlugin.PLUGIN_ID, WEB_HOME);
	public static final QualifiedName BMQN = new QualifiedName(AuroraPlugin.PLUGIN_ID, BM_HOME);
	public static final QualifiedName LogQN = new QualifiedName(AuroraPlugin.PLUGIN_ID, LOG_HOME);
	public static final QualifiedName DebugModeQN = new QualifiedName(AuroraPlugin.PLUGIN_ID, DebugMode);
	private static final String LogAttrName = "logPath";
	private Text localWebUrlText;
	private Text webHomeText;
	private Text bmHomeText;
	private Text logHomeText;
	private Button debugButton;
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
		localWebUrlText = new Text(content, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		localWebUrlText.setLayoutData(gridData);
		try {
			String localWebUrl = getProject().getPersistentProperty(LoclaUrlHomeQN);
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

		// logDir
		Label logDirLabel = new Label(content, SWT.NONE);
		logDirLabel.setText("Log主目录");
		logHomeText = new Text(content, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		logHomeText.setLayoutData(gridData);
		String logDir = null;
		try {
			logDir = getProject().getPersistentProperty(LogQN);
			if (filtEmpty(logDir) != null) {
				logHomeText.setText(logDir);
			} else {
				logDir = ProjectUtil.autoGetLogHome(getProject());
				if (filtEmpty(logDir) != null) {
					logHomeText.setText(logDir);
				}
			}
		} catch (CoreException e) {
			DialogUtil.showExceptionMessageBox(e);
		} catch (ApplicationException e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		Button logBrowseButton = new Button(content, SWT.PUSH);
		logBrowseButton.setText("浏览(&L)..");
		logBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
				if (logHomeText.getText() != null) {
					dialog.setFilterPath(logHomeText.getText());
				}
				String logDir = dialog.open();
				String errorMessage = validLogHome(logDir);
				if (errorMessage != null) {
					if (DialogUtil.showConfirmDialogBox("校验不通过：" + errorMessage + AuroraResourceUtil.LineSeparator
							+ "是否仍然继续设置?") != SWT.OK) {
						return;
					}
				}
				logHomeText.setText(logDir);
			}
		});
		// webDir
		Label webDirGroup = new Label(content, SWT.NONE);
		webDirGroup.setText("Web主目录");
		webHomeText = new Text(content, SWT.NONE);
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
			DialogUtil.showExceptionMessageBox(e);
		} catch (SystemException e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		Button webBrowseButton = new Button(content, SWT.PUSH);
		webBrowseButton.setText("浏览(&W)..");
		webBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(Display.getCurrent().getActiveShell(),
						getProject(), false, LocaleMessage.getString("please.select.the.path"));
				if (dialog.open() == ContainerSelectionDialog.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 1) {
						IPath selectionPath = (IPath) result[0];
						String errorMessage = validWebHome(getProject(), selectionPath);
						if (errorMessage != null) {
							if (DialogUtil.showConfirmDialogBox("校验不通过：" + errorMessage
									+ AuroraResourceUtil.LineSeparator + "是否仍然继续设置?") != SWT.OK) {
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
		bmHomeText = new Text(content, SWT.NONE);
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
			DialogUtil.showExceptionMessageBox(e);
		} catch (SystemException e) {
			DialogUtil.showExceptionMessageBox(e);
		}

		Button bmBrowseButton = new Button(content, SWT.PUSH);
		bmBrowseButton.setText(LocaleMessage.getString("openBrowse"));
		bmBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				ContainerSelectionDialog dialog = new ContainerSelectionDialog(Display.getCurrent().getActiveShell(),
						getProject(), false, LocaleMessage.getString("please.select.the.path"));
				if (dialog.open() == ContainerSelectionDialog.OK) {
					Object[] result = dialog.getResult();
					if (result.length == 1) {
						IPath selectionPath = (IPath) result[0];
						String errorMessage = validBMHome(getProject(), selectionPath);
						if (errorMessage != null) {
							if (DialogUtil.showConfirmDialogBox("校验不通过：" + errorMessage
									+ AuroraResourceUtil.LineSeparator + "是否仍然继续设置?") != SWT.OK) {
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
				if (performOk()) {
					String errorMessage = validWebHome(getProject(), new Path(webHomeText.getText()));
					if (errorMessage != null) {
						if (DialogUtil.showConfirmDialogBox("校验不通过：" + errorMessage + AuroraResourceUtil.LineSeparator
								+ "是否仍然继续设置?") != SWT.OK) {
							return;
						}
					} else {
						DialogUtil.showMessageBox(SWT.ICON_INFORMATION, "OK", "数据库连接测试成功!");
					}
				}

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		debugButton = new Button(content, SWT.CHECK);
		debugButton.setText("调试模式 ( 记录IDE运行日志,不建议开启. )");
		String debugMode = null;
		try {
			debugMode = getProject().getPersistentProperty(DebugModeQN);
		} catch (CoreException e) {
			DialogUtil.showExceptionMessageBox(e);
		}
		if ("true".equals(debugMode)) {
			debugButton.setSelection(true);
		}
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		debugButton.setLayoutData(gridData);
		return content;
	}

	public boolean performOk() {
		IProject project = getProject();
		if (webHomeText.getText() == null || "".equals(webHomeText.getText())) {
			setErrorMessage("请先设置Web主目录.");
			return false;
		}
		if (webHomeText.getText() != null) {
			try {
				project.setPersistentProperty(WebQN, webHomeText.getText());
			} catch (CoreException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}
		if (bmHomeText.getText() != null) {
			try {
				project.setPersistentProperty(BMQN, bmHomeText.getText());
			} catch (CoreException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}
		if (logHomeText.getText() != null) {
			try {
				IContainer webHome = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(
						webHomeText.getText());
				String logDir = logHomeText.getText();
				project.setPersistentProperty(LogQN, logDir);
				IResource coreConfigFile = AuroraResourceUtil.getResource(webHome, AuroraConstant.CoreConfigFileName);
				IResource logConfigFile = AuroraResourceUtil.getResource(webHome, AuroraConstant.LogConfigFileName);
				if (coreConfigFile != null) {
					setLogDirToConfigFile(coreConfigFile, logDir);
				}
				if (logConfigFile != null) {
					setLogDirToConfigFile(logConfigFile, logDir);
				}
			} catch (CoreException e) {
				DialogUtil.showExceptionMessageBox(e);
			} catch (ApplicationException e) {
				DialogUtil.showExceptionMessageBox(e);
			}
		}
		try {
			project.setPersistentProperty(DebugModeQN, String.valueOf(debugButton.getSelection()));
			project.setPersistentProperty(LoclaUrlHomeQN, localWebUrlText.getText());
		} catch (CoreException e) {
			DialogUtil.showExceptionMessageBox(e);
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

	public static void setLogDirToConfigFile(IResource coreConfigFile, String logDir) throws ApplicationException {
		String coreConfigFileLocalPath = coreConfigFile.getLocation().toOSString();
		File coreConfig = new File(coreConfigFileLocalPath);
		if (!coreConfig.exists())
			throw new ApplicationException(coreConfigFileLocalPath + "在文件系统中不存在!");
		CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
		CompositeMap data;
		try {
			data = loader.loadByFullFilePath(coreConfigFileLocalPath);
			if (data == null) {
				throw new ApplicationException(coreConfigFileLocalPath + "文件为空!");
			}
			data.put(LogAttrName, logDir);
			XMLOutputter.saveToFile(coreConfig, data);
		} catch (IOException e) {
			throw new ApplicationException(coreConfigFileLocalPath + "在文件系统中不存在!", e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查" + coreConfigFileLocalPath + "格式是否正确!", e);
		}
	}

	public static String getLogDirFromCoreConfigFile(IResource coreConfigFile) throws ApplicationException {
		String coreConfigFileLocalPath = coreConfigFile.getLocation().toOSString();
		File coreConfig = new File(coreConfigFileLocalPath);
		if (!coreConfig.exists())
			throw new ApplicationException(coreConfigFileLocalPath + "在文件系统中不存在!");
		CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
		CompositeMap data;
		try {
			data = loader.loadByFullFilePath(coreConfigFileLocalPath);
		} catch (IOException e) {
			throw new ApplicationException(coreConfigFileLocalPath + "在文件系统中不存在!", e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查" + coreConfigFileLocalPath + "格式是否正确!", e);
		}
		if (data == null) {
			throw new ApplicationException(coreConfigFileLocalPath + "文件为空!");
		}
		String logPath = data.getString(LogAttrName);
		return logPath;
	}

	public static String validWebHome(IProject project, IPath path) {
		IResource selectionResource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
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
			DBConnectionUtil.testDBConnection(locationPath);
		} catch (ApplicationException e) {
			return ExceptionUtil.getExceptionTraceMessage(e);
		}
		return null;
	}

	public static String validBMHome(IProject project, IPath path) {
		String classesDir = "classes";
		IResource selectionResource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
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

	public static String validLogHome(String logLocalPath) {
		File logFile = new File(logLocalPath);
		if (!logFile.exists()) {
			return "文件系统中不存在此目录!";
		}
		return null;
	}
}
