package aurora.ide.helpers;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.eclipse.core.resources.IProject;

import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;

public class UncertainEngineUtil {
	private static HashMap project_engine = new HashMap();

	public static UncertainEngine initUncertainProject(IProject project) throws ApplicationException {
		Object obj = project_engine.get(project);
		if (obj != null)
			return (UncertainEngine) obj;
		String webHome = ProjectUtil.getWebHomeLocalPath(project);
		UncertainEngine ue = initUncertainProject(webHome);
		project_engine.put(project, ue);
		return ue;
	}

	public static boolean testDBConnection(IProject project, String webHome) throws ApplicationException {
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
			e.printStackTrace();
			throw new ApplicationException("获取数据库连接失败!请查看" + AuroraConstant.DbConfigFileName + "是否配置正确.", e);
		}
		return true;
	}
	private static UncertainEngine initUncertainProject(String webHome) throws ApplicationException {
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
	public static UncertainEngine getUncertainEngine(){
		return new UncertainEngineIDE();
	}
	public static UncertainEngine getUncertainEngine(String excludePackage){
		return new UncertainEngineIDE(excludePackage);
	}
	public static UncertainEngine getUncertainEngine(File config_dir){
		return new UncertainEngineIDE(config_dir);
	}
	public static UncertainEngine getUncertainEngine(File config_dir,String excludePackage){
		return new UncertainEngineIDE(config_dir,excludePackage);
	}
}
