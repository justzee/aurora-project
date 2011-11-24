package aurora.ide.statistics.viewer;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aurora.ide.AuroraPlugin;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.statistics.DBManager;
import aurora.statistics.DatabaseAction;
import aurora.statistics.Statistician;
import aurora.statistics.map.StatisticsResult;
import aurora.statistics.model.StatisticsProject;

public class LoadFromDBJob extends Job {

	private Statistician statistician;

	public LoadFromDBJob(Statistician statistician) {
		super("从数据库加载数据");
		this.statistician = statistician;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IProject project = AuroraPlugin.getWorkspace().getRoot()
				.getProject("hr_aurora");
		DBManager dm = new DBManager(project);
		Connection connection;
		//TODO 未完 待续。。。。
		try {
			connection = dm.getConnection();
			// DatabaseAction.dropTables(connection);
			StatisticsProject[] readAllProject = DatabaseAction
					.readAllProject(connection);
			Statistician s = new Statistician(readAllProject[0], null);
			StatisticsResult read = s.read(connection);
			connection.close();
//			a.d.d();
		} catch (ApplicationException e ) {
			showMessage(e.getMessage());
		} catch (SQLException e) {
			// java.sql.SQLException: ORA-00942: 表或视图不存在
			if (e.getMessage().startsWith("ORA-00942"))
				showMessage("表或视图不存在,请先进行保存操作");
			e.printStackTrace();
		}

		return Status.OK_STATUS;
	}

	private void showMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				MessageDialog.openInformation(Display.getDefault()
						.getActiveShell(), "统计分析", message);

			}

		});

	}
}
