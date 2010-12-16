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
import uncertain.ide.util.LocaleMessage;
import uncertain.ocm.IObjectRegistry;

public class AuroraDataBase implements IRunnableWithProgress {
	private Connection connection;
	private UncertainEngine uncertainEngine;

	private AuroraDataBase(UncertainEngine uncertainEngine) {
		this.uncertainEngine = uncertainEngine;
	}
	private Connection initDBConnection(UncertainEngine uncertainEngine)
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
				LocaleMessage.getString("try.to.get.database.connection.please.wait"),
				IProgressMonitor.UNKNOWN);
		try {
			connection = initDBConnection(uncertainEngine);
		} catch (Exception e) {
			/**
			 * Must use throw ,but not CustomDialog,because CustomDialog will
			 * create new Shell, that action will cause error!
			 */
			throw new RuntimeException(e);
		}
		monitor.done();

	}

	private Connection getConnection() {
		return connection;
	}

	public static Connection getDBConnection(String projectFullPath)
			throws Exception {
		UncertainEngine engine = AuroraProject.getUncertainEngine(projectFullPath);
		return getDBConnection(engine);
	}

	public static Connection getDBConnection(IProject project) throws Exception {
		UncertainEngine engine = AuroraProject.getUncertainEngine(project);
		return getDBConnection(engine);
	}

	public static Connection getDBConnection(UncertainEngine uncertainEngine)
			throws Exception {
		AuroraDataBase up = new AuroraDataBase(uncertainEngine);
		new ProgressMonitorDialog(new Shell()).run(true, true, up);
		return up.getConnection();
	}

}
