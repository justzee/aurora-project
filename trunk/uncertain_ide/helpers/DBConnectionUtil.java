package helpers;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.eclipse.core.resources.IProject;

import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;

public class DBConnectionUtil {

	private static HashMap project_connections = new HashMap();
	public static Connection getDBConnection(IProject project) throws ApplicationException {
		if (project == null)
			throw new ApplicationException("项目参数不能为空");
		Connection conn = null;
		Object obj = project_connections.get(project);
		if (obj != null) {
			conn = (Connection) obj;
			try {
				if (!conn.isClosed())
					return conn;
			} catch (SQLException e) {
				throw new SystemException(e);
			}
		}
		UncertainEngine ue = initUncertainProject(project);
		if (ue == null)
			throw new ApplicationException("获取UncertainProject失败!");
		IObjectRegistry mObjectRegistry = ue.getObjectRegistry();
		DataSource ds = (DataSource) mObjectRegistry.getInstanceOfType(DataSource.class);
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ApplicationException("获取数据库连接失败!请查看" + AuroraConstant.DbConfigFileName + "是否配置正确.", e);
		}
		project_connections.put(project, conn);
		return conn;
	}

	public static UncertainEngine initUncertainProject(IProject project) throws ApplicationException {
			String webHome =  ProjectUtil.getWebHomeLocalPath(project);
			return initUncertainProject(webHome);
	}
	public static UncertainEngine initUncertainProject(String webHome) throws ApplicationException {
		try {
			if (webHome == null)
				return null;
			File home_path = new File(webHome);
			File config_path = new File(home_path, "WEB-INF");
			DBEngineInitiator ei = new DBEngineInitiator(home_path, config_path);
			ei.init();
			UncertainEngine uncertainEngine = ei.getUncertainEngine();
			return uncertainEngine;
		} catch (Throwable e) {
			throw new ApplicationException("启用EngineInitiator失败!", e);
		}
	}
	public static boolean testDBConnection(String webHome) throws ApplicationException {
		if (webHome == null)
			throw new ApplicationException("Web目录不能为空");
		UncertainEngine ue = initUncertainProject(webHome);
		if (ue == null)
			throw new ApplicationException("获取UncertainProject失败!");
		IObjectRegistry mObjectRegistry = ue.getObjectRegistry();
		DataSource ds = (DataSource) mObjectRegistry.getInstanceOfType(DataSource.class);
		try {
			Connection conn = ds.getConnection();
			conn.close();
		} catch (SQLException e) {
			throw new ApplicationException("获取数据库连接失败!请查看" + AuroraConstant.DbConfigFileName + "是否配置正确.", e);
		}
		return true;
	}

}
