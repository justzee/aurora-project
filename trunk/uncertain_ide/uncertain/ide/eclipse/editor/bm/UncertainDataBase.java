package uncertain.ide.eclipse.editor.bm;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import uncertain.core.UncertainEngine;
import uncertain.ide.eclipse.editor.widgets.CustomDialog;
import uncertain.ide.eclipse.wizards.UncertainWebAppPathDialog;
import uncertain.ocm.IObjectRegistry;

public class UncertainDataBase implements IRunnableWithProgress {
	public static final String uncertain_project_dir = "uncertain_project_dir";
	public static String projectFile = "uncertain.properties";
	private IProject project;
	private String projectFullPath;
	private Connection connection;
	private UncertainEngine uncertainEngine;
	public UncertainDataBase(IProject project) {
		this.project = project;
	}

	public UncertainDataBase(String projectFullPath) {
		this.projectFullPath = projectFullPath;
	}

	public UncertainDataBase(UncertainEngine uncertainEngine) {
		this.uncertainEngine = uncertainEngine;
	}

	public Connection initDBConnection(String projectFullPath) throws Exception {
		UncertainProject uncertainProject = new UncertainProject(
				projectFullPath);
		 UncertainEngine uncertainEngine = uncertainProject.initUncertainProject();
		return initDBConnection(uncertainEngine);
	}

	private Connection initDBConnection(IProject project)  throws Exception{
		UncertainProject uncertainProject = new UncertainProject(project);
		UncertainEngine uncertainEngine = uncertainProject.initUncertainProject();
		return initDBConnection(uncertainEngine);
	}

	public Connection initDBConnection(UncertainEngine uncertainEngine)
			throws SQLException {
		IObjectRegistry mObjectRegistry = uncertainEngine.getObjectRegistry();
		DataSource ds = (DataSource) mObjectRegistry
				.getInstanceOfType(DataSource.class);
		Connection conn = ds.getConnection();
		return conn;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(
				"Try to Get Aurora DataBase Connection,Please wait...",
				IProgressMonitor.UNKNOWN);
		try {
			connection = initDBConnection();
		} catch (Exception e) {
			/**
			 * Must use throw ,but not CustomDialog,because CustomDialog will
			 * create new Shell, that action will cause error!
			 */
			throw new RuntimeException(e);
		}
		monitor.done();

	}

	public Connection initDBConnection() throws Exception {
		Connection connection = null;
		if (uncertainEngine != null)
			connection = initDBConnection(uncertainEngine);
		else if (project != null)
			connection = initDBConnection(project);
		else if (projectFullPath != null) {
			connection = initDBConnection(projectFullPath);
		}
		return connection;
	}

	private Connection getConnection() {
		return connection;
	}

	public static Connection getDBConnection(String projectFullPath)
			throws Exception {
		UncertainDataBase up = new UncertainDataBase(projectFullPath);
		new ProgressMonitorDialog(null).run(true, false, up);
		return up.getConnection();
	}

	public static Connection getDBConnection(IProject project) throws Exception {
		UncertainDataBase up = new UncertainDataBase(project);
		try {
			new ProgressMonitorDialog(null).run(true, false, up);
		} catch (InvocationTargetException e) {
			String message = CustomDialog.getExceptionMessage(e);
			if(message != null && (message.indexOf(UncertainProject.prejectFileNotExistsErrorMessage)!=-1)){
				if(UncertainWebAppPathDialog.createWebAppDialog(project)){
					new ProgressMonitorDialog(null).run(true, false, up);
				}
			}
			else
				throw new InvocationTargetException(e.getCause());
		}
		return up.getConnection();
	}

	public static Connection getDBConnection(UncertainEngine uncertainEngine)
			throws Exception {
		Shell shell = new Shell();
		UncertainDataBase up = new UncertainDataBase(uncertainEngine);
		new ProgressMonitorDialog(shell).run(true, false, up);
		return up.getConnection();
	}

}
