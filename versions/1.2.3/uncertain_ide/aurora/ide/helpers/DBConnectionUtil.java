package aurora.ide.helpers;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.eclipse.core.resources.IProject;

import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;

public class DBConnectionUtil {


	public static Connection getDBConnection(IProject project) throws ApplicationException {
		if (project == null)
			throw new ApplicationException("项目参数不能为空");
		Connection conn = null;
		UncertainEngine ue = UncertainEngineUtil.initUncertainProject(project);
		if (ue == null)
			throw new ApplicationException("获取UncertainProject失败!");
		conn = getConnection(ue);
		return conn;
	}
	public static Connection getConnection(UncertainEngine ue) throws ApplicationException{
		if(ue == null)
			return null;
		Connection conn = null;
		IObjectRegistry mObjectRegistry = ue.getObjectRegistry();
		DataSource ds = (DataSource) mObjectRegistry.getInstanceOfType(DataSource.class);
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ApplicationException("获取数据库连接失败!请查看" + AuroraConstant.DbConfigFileName + "是否配置正确.", e);
		}
		return conn ;
	}
}
