package uncertain.ide.eclipse.project.propertypage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.xml.sax.SAXException;

import aurora.ide.AuroraConstant;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.composite.XMLOutputter;
import uncertain.core.EngineInitiator;
import uncertain.core.UncertainEngine;
import uncertain.ide.Activator;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.AuroraResourceUtil;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;
import uncertain.ide.help.SystemException;
import uncertain.ocm.IObjectRegistry;

public class ProjectPropertyPage extends PropertyPage {
	public static final String PropertyId = "uncertain.ide.eclipse.projectproperty";
	private static final String WEB_BASE_DIR = "WEB_BASE_DIR";
	private static final String BM_BASE_DIR = "BM_BASE_DIR";
	private static final String LOG_BASE_DIR = "LOG_BASE_DIR";
	private static final String DebugMode = "DEBUG_MODE";
	public static final QualifiedName WebQN = new QualifiedName(
			Activator.PLUGIN_ID, WEB_BASE_DIR);
	public static final QualifiedName BMQN = new QualifiedName(
			Activator.PLUGIN_ID, BM_BASE_DIR);
	public static final QualifiedName LogQN = new QualifiedName(
			Activator.PLUGIN_ID, LOG_BASE_DIR);
	public static final QualifiedName DebugModeQN = new QualifiedName(
			Activator.PLUGIN_ID, DebugMode);
	private static final String LogAttrName = "logPath";
	private Text webDirText;
	private Text bmDirText;
	private Text logDirText;
	private Button debugButton;
	protected Control createContents(Composite parent) {
		Composite content = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		content.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		content.setLayoutData(data);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		// webDir
		Group webDirGroup = new Group(content, SWT.NONE);
		gridData.horizontalSpan = 4;
		webDirGroup.setLayoutData(gridData);
		webDirGroup.setText("Web目录");
		layout = new GridLayout();
		layout.numColumns = 2;
		webDirGroup.setLayout(layout);

		webDirText = new Text(webDirGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		webDirText.setLayoutData(gridData);
		String webDir = null;
		try {
			webDir = getProject().getPersistentProperty(WebQN);
			if (filtEmpty(webDir) != null) {
				webDirText.setText(webDir);
			} else {
				webDir = autoGetWebDir(getProject());
				if (filtEmpty(webDir) != null) {
					webDirText.setText(webDir);
				}
			}
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		} catch (SystemException e) {
			CustomDialog.showErrorMessageBox(e);
		}

		Button webBrowseButton = new Button(webDirGroup, SWT.PUSH);
		webBrowseButton.setText(LocaleMessage.getString("openBrowse"));
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
						String errorMessage = validWebDir(getProject(),
								selectionPath);
						if (errorMessage != null) {
							if (CustomDialog.showConfirmDialogBox("校验不通过："
									+ errorMessage
									+ AuroraResourceUtil.LineSeparator
									+ "是否仍然继续设置?") != SWT.OK) {
								return;
							}
						}
						webDirText.setText(selectionPath.toString());
					}
				}
			}
		});
		// BMDir
		Group bmDirGroup = new Group(content, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		bmDirGroup.setLayoutData(gridData);
		bmDirGroup.setText("BM目录");
		layout = new GridLayout();
		layout.numColumns = 2;
		bmDirGroup.setLayout(layout);

		bmDirText = new Text(bmDirGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		bmDirText.setLayoutData(gridData);

		String bmDir = null;
		try {
			bmDir = getProject().getPersistentProperty(BMQN);
			if (filtEmpty(bmDir) != null) {
				bmDirText.setText(bmDir);
			} else {
				bmDir = autoGetBMDir(getProject());
				if (filtEmpty(bmDir) != null) {
					bmDirText.setText(bmDir);
				}
			}
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		} catch (SystemException e) {
			CustomDialog.showErrorMessageBox(e);
		}

		Button bmBrowseButton = new Button(bmDirGroup, SWT.PUSH);
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
						String errorMessage = validBMDir(getProject(),
								selectionPath);
						if (errorMessage != null) {
							if (CustomDialog.showConfirmDialogBox("校验不通过："
									+ errorMessage
									+ AuroraResourceUtil.LineSeparator
									+ "是否仍然继续设置?") != SWT.OK) {
								return;
							}
						}
						bmDirText.setText(selectionPath.toString());
					}
				}
			}
		});
		// logDir
		Group logDirGroup = new Group(content, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		logDirGroup.setLayoutData(gridData);
		logDirGroup.setText("Aurora运行Log目录");
		layout = new GridLayout();
		layout.numColumns = 2;
		logDirGroup.setLayout(layout);

		logDirText = new Text(logDirGroup, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		logDirText.setLayoutData(gridData);

		String logDir = null;
		try {
			logDir = getProject().getPersistentProperty(LogQN);
			if (filtEmpty(logDir) != null) {
				logDirText.setText(logDir);
			} else {
				logDir = autoGetLogDir(getProject());
				if (filtEmpty(logDir) != null) {
					logDirText.setText(logDir);
				}
			}
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		} catch (ApplicationException e) {
			CustomDialog.showErrorMessageBox(e);
		}

		Button logBrowseButton = new Button(logDirGroup, SWT.PUSH);
		logBrowseButton.setText(LocaleMessage.getString("openBrowse"));
		logBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dialog = new DirectoryDialog(Display
						.getCurrent().getActiveShell());
				if (logDirText.getText() != null) {
					dialog.setFilterPath(logDirText.getText());
				}
				String logDir = dialog.open();
				String errorMessage = validLogDir(logDir);
				if (errorMessage != null) {
					if (CustomDialog.showConfirmDialogBox("校验不通过："
							+ errorMessage + AuroraResourceUtil.LineSeparator
							+ "是否仍然继续设置?") != SWT.OK) {
						return;
					}
				}
				logDirText.setText(logDir);
			}
		});
		debugButton = new Button(content, SWT.CHECK);
		debugButton.setText("调试模式 ( 记录IDE运行日志,不建议开启. )");
		String debugMode = null;
		try {
			debugMode = getProject().getPersistentProperty(DebugModeQN);
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		if("true".equals(debugMode)){
			debugButton.setSelection(true);
		}
		return content;
	}

	public boolean performOk() {
		IProject project = getProject();
		if (webDirText.getText() == null ||"".equals(webDirText.getText())) {
			setErrorMessage("请先设置web目录.");
			return false;
		}
		if (webDirText.getText() != null) {
			try {
				project.setPersistentProperty(WebQN, webDirText.getText());
			} catch (CoreException e) {
				CustomDialog.showErrorMessageBox(e);
			}
		}
		if (bmDirText.getText() != null) {
			try {
				project.setPersistentProperty(BMQN, bmDirText.getText());
			} catch (CoreException e) {
				CustomDialog.showErrorMessageBox(e);
			}
		}
		if (logDirText.getText() != null) {
			try {
				IContainer webDir = (IContainer)ResourcesPlugin.getWorkspace().getRoot().findMember(webDirText.getText());
				String logDir = logDirText.getText();
				project.setPersistentProperty(LogQN, logDir);
				IResource coreConfigFile = getResource(webDir,
						AuroraConstant.CoreConfigFileName);
				IResource logConfigFile = getResource(webDir,
						AuroraConstant.LogConfigFileName);
				if (coreConfigFile != null) {
					setLogDirToConfigFile(coreConfigFile, logDir);
				}
				if (logConfigFile != null) {
					setLogDirToConfigFile(logConfigFile, logDir);
				}
			} catch (CoreException e) {
				CustomDialog.showErrorMessageBox(e);
			} catch (ApplicationException e) {
				CustomDialog.showErrorMessageBox(e);
			}
		}
		try {
			project.setPersistentProperty(DebugModeQN, String.valueOf(debugButton.getSelection()));
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		return true;

	}
	public static boolean isDebugMode(IProject project){
		if(project == null)
			return false;
		try {
			if("true".equals(project.getPersistentProperty(DebugModeQN)))
				return true;
		} catch (CoreException e) {
			CustomDialog.showErrorMessageBox(e);
		}
		return false;
	}

	private IProject getProject() {
		return (IProject) getElement();
	}

	private static String filtEmpty(String str) {
		if ("".equals(str))
			return null;
		return str;
	}

	public static String getWebBaseDir(IProject project)
			throws ApplicationException {
		if (project == null)
			throw new ApplicationException("project参数是空！");
		String webBaseDir = null;
		try {
			webBaseDir = project.getPersistentProperty(WebQN);

			if (filtEmpty(webBaseDir) == null) {
				webBaseDir = autoGetWebDir(project);
				if (filtEmpty(webBaseDir) != null) {
					return webBaseDir;
				}
				if (openProjectPropertyPage(project) == Window.OK) {
					webBaseDir = project.getPersistentProperty(WebQN);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		return webBaseDir;
	}

	public static String getBMBaseDir(IProject project)
			throws ApplicationException {
		if (project == null)
			throw new ApplicationException("project参数是空！");
		String bmBaseDir = null;
		try {
			bmBaseDir = project.getPersistentProperty(BMQN);
			if (filtEmpty(bmBaseDir) == null) {
				bmBaseDir = autoGetBMDir(project);
				if (filtEmpty(bmBaseDir) != null) {
					return bmBaseDir;
				}
				if (openProjectPropertyPage(project) == Window.OK) {
					bmBaseDir = project.getPersistentProperty(BMQN);
				}
			}
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		if (filtEmpty(bmBaseDir) == null) {
			throw new ApplicationException("设置BM目录失败！");
		}
		return bmBaseDir;
	}

	public static String getWebBaseLocalDir(IProject project)
			throws ApplicationException {
		String webBaseDir = getWebBaseDir(project);
		if (filtEmpty(webBaseDir) == null)
			throw new ApplicationException("获取Web目录失败！");
		IPath path = new Path(webBaseDir);
		return AuroraResourceUtil.getLocalPathFromIPath(path);
	}

	public static int openProjectPropertyPage(IProject project) {
		PreferenceDialog propertyDialog = PreferencesUtil
				.createPropertyDialogOn(Display.getCurrent().getActiveShell(),
						project, PropertyId, new String[] { PropertyId }, null);
		return propertyDialog.open();
	}

	public static String getBMBaseLocalDir(IProject project)
			throws ApplicationException {
		String bmBaseDir = getBMBaseDir(project);
		if (filtEmpty(bmBaseDir) == null)
			throw new ApplicationException("获取BM目录失败！");
		IPath path = new Path(bmBaseDir);
		return AuroraResourceUtil.getLocalPathFromIPath(path);
	}

	private static String autoGetWebDir(IProject project)
			throws SystemException {
		IResource webInf = getResource(project, "web-inf");
		if (webInf == null)
			return null;
		else {
			IResource webDir = webInf.getParent();
			if (webDir.exists()) {
				String errorMessage = validWebDir(project, webDir.getFullPath());
				if (errorMessage == null) {
					String webDirPath = webDir.getFullPath().toString();
					try {
						project.setPersistentProperty(WebQN, webDirPath);
					} catch (CoreException e) {
						throw new SystemException(e);
					}
					return webDirPath;
				}
			}
		}
		return null;
	}

	private static String autoGetBMDir(IProject project) throws SystemException {
		IResource classesDir = getResource(project, "classes");
		if (classesDir == null)
			return null;
		else {
			if (classesDir.exists()) {
				String errorMessage = validBMDir(project, classesDir
						.getFullPath());
				if (errorMessage == null) {
					String classesDirPath = classesDir.getFullPath().toString();
					try {
						project.setPersistentProperty(BMQN, classesDirPath);
					} catch (CoreException e) {
						throw new SystemException(e);
					}
					return classesDirPath;
				}
			}
		}
		return null;
	}

	private static String autoGetLogDir(IProject project)
			throws ApplicationException {
		IResource coreConfigFile = getResource(project,
				AuroraConstant.CoreConfigFileName);
		IResource logConfigFile = getResource(project,
				AuroraConstant.LogConfigFileName);
		if (coreConfigFile == null)
			return null;
		else {
			if (coreConfigFile.exists()) {
				String logDir = getLogDirFromCoreConfigFile(coreConfigFile);
				if (logDir != null) {
					String errorMessage = validLogDir(logDir);
					if (errorMessage == null) {
						try {
							project.setPersistentProperty(LogQN, logDir);
						} catch (CoreException e) {
							throw new SystemException(e);
						}
						if (logConfigFile != null) {
							setLogDirToConfigFile(logConfigFile, logDir);
						}
					}
					return logDir;
				}
			}
		}
		return null;
	}

	private static void setLogDirToConfigFile(IResource coreConfigFile,
			String logDir) throws ApplicationException {
		String coreConfigFileLocalPath = coreConfigFile.getLocation()
				.toOSString();
		File coreConfig = new File(coreConfigFileLocalPath);
		if (!coreConfig.exists())
			throw new ApplicationException(coreConfigFileLocalPath
					+ "在文件系统中不存在!");
		CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
		CompositeMap data;
		try {
			data = loader.loadByFullFilePath(coreConfigFileLocalPath);
			if (data == null) {
				throw new ApplicationException(coreConfigFileLocalPath
						+ "文件为空!");
			}
			data.put(LogAttrName, logDir);
			XMLOutputter.saveToFile(coreConfig, data);
		} catch (IOException e) {
			throw new ApplicationException(coreConfigFileLocalPath
					+ "在文件系统中不存在!", e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查" + coreConfigFileLocalPath
					+ "格式是否正确!", e);
		}

	}

	private static String getLogDirFromCoreConfigFile(IResource coreConfigFile)
			throws ApplicationException {
		String coreConfigFileLocalPath = coreConfigFile.getLocation()
				.toOSString();
		File coreConfig = new File(coreConfigFileLocalPath);
		if (!coreConfig.exists())
			throw new ApplicationException(coreConfigFileLocalPath
					+ "在文件系统中不存在!");
		CompositeLoader loader = AuroraResourceUtil.getCompsiteLoader();
		CompositeMap data;
		try {
			data = loader.loadByFullFilePath(coreConfigFileLocalPath);
		} catch (IOException e) {
			throw new ApplicationException(coreConfigFileLocalPath
					+ "在文件系统中不存在!", e);
		} catch (SAXException e) {
			throw new ApplicationException("请检查" + coreConfigFileLocalPath
					+ "格式是否正确!", e);
		}
		if (data == null) {
			throw new ApplicationException(coreConfigFileLocalPath + "文件为空!");
		}
		String logPath = data.getString(LogAttrName);
		return logPath;
	}

	private static IResource getResource(IContainer parent, String resourceName)
			throws SystemException {
		IResource[] childs;
		try {
			childs = parent.members();
		} catch (CoreException e) {
			throw new SystemException(e);
		}
		for (int i = 0; i < childs.length; i++) {
			IResource child = childs[i];
			if (resourceName.equals(child.getName().toLowerCase())) {
				return child;
			}
			if (child instanceof IContainer) {
				IResource result = getResource((IContainer) child, resourceName);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	private static String validWebDir(IProject project, IPath path) {
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
			getDBConnection(locationPath);
		} catch (Exception e) {
			return CustomDialog.getExceptionMessage(e);
		}
		return null;
	}

	private static String validBMDir(IProject project, IPath path) {
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

	private static String validLogDir(String logLocalPath) {
		File logFile = new File(logLocalPath);
		if (!logFile.exists()) {
			return "文件系统中不存在此目录!";
		}
		return null;
	}

	private static UncertainEngine initUncertainProject(String webBasePath)
			throws ApplicationException {
		if (webBasePath == null)
			return null;
		File home_path = new File(webBasePath);
		File config_path = new File(home_path, "WEB-INF");
		EngineInitiator ei = new EngineInitiator(home_path, config_path);
		try {
			ei.init();
		} catch (Exception e) {
			throw new ApplicationException("请检查"
					+ AuroraConstant.CoreConfigFileName + "文件的配置!", e);
		}
		UncertainEngine uncertainEngine = ei.getUncertainEngine();
		return uncertainEngine;
	}

	private static Connection getDBConnection(String webBasePath)
			throws ApplicationException {
		UncertainEngine ue = initUncertainProject(webBasePath);
		if (ue == null)
			throw new ApplicationException("请检查"
					+ AuroraConstant.CoreConfigFileName + "文件的配置!");
		IObjectRegistry mObjectRegistry = ue.getObjectRegistry();
		DataSource ds = (DataSource) mObjectRegistry
				.getInstanceOfType(DataSource.class);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ApplicationException("请检查"
					+ AuroraConstant.DbConfigFileName + "文件的配置!", e);
		}
		return conn;
	}
}
