package aurora.ide.statistics;

import java.sql.Connection;

import org.eclipse.core.resources.IProject;

import uncertain.core.UncertainEngine;
import aurora.ide.helpers.ApplicationException;
import aurora.ide.helpers.DBConnectionUtil;
import aurora.ide.helpers.UncertainEngineUtil;

public class DBManager {
	private IProject project;
	public DBManager(IProject project){
		this.project = project;
		
	}
	public Connection getConnection() throws ApplicationException {
		UncertainEngine uncertainEngine = UncertainEngineUtil.initUncertainProject(project);
		Connection connection = DBConnectionUtil.getConnection(uncertainEngine);
		return connection;
	}
}
