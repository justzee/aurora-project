package aurora.ide.editor.textpage.action;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;

import aurora.ide.api.composite.map.Comment;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.FileUtil;
import aurora.ide.helpers.PathUtil;

public class RegisterSql {
	// 页面注册
	private StringBuilder pageRegisterSql = new StringBuilder();
	private static final String PAGE_REGISTER = "#PAGE_REGISTER#";
	private static final String PAGE_PATH = "#PAGE_PATH#";
	private static final String PAGE_NAME = "#PAGE_NAME#";
	private static final String PAGE_REGISTER_SQL = "sys_service_pkg.sys_service_load('#PAGE_PATH#','#PAGE_NAME#',1,1,0);";
	
	// 功能定义
	private StringBuilder functionRegisterSql = new StringBuilder();
	private static final String MODULE_REGISTER = "#MODULE_REGISTER#";
	private static final String FUNCTION_REGISTER = "#FUNCTION_REGISTER#";
	private static final String MODULE_CODE = "#MODULE_CODE#";
	private static final String MODULE_NAME = "#MODULE_NAME#";
	private static final String FUNCTION_CODE = "#FUNCTION_CODE#";
	private static final String FUNCTION_NAME = "#FUNCTION_NAME#";
	private static final String FUNCTION_ORDER = "#FUNCTION_ORDER#";
	private static final String HOST_PATH = "#HOST_PATH#";
	private static final String MODULE_REGISTER_SQL = "sys_function_pkg.sys_function_load('#MODULE_CODE#','#MODULE_NAME#','','G','','','','ZHS');";
	private static final String FUNCTION_REGISTER_SQL = "sys_function_pkg.sys_function_load('#FUNCTION_CODE#','#FUNCTION_NAME#','#MODULE_CODE#','F','#HOST_PATH#','#FUNCTION_ORDER#','','ZHS');";

	// 分配页面
	private StringBuilder serviceRegisterSql = new StringBuilder();
	private static final String LOAD_SERVICE = "#LOAD_SERVICE#";
	private static final String LOAD_SERVICE_SQL = "sys_function_service_pkg.load_service('#HOST_PATH#','#PAGE_PATH#');";

	// 分配BM
	private StringBuilder bmRegisterSql = new StringBuilder();
	private static final String REGISTER_BM = "#REGISTER_BM#";
	private static final String BM_PATH = "#BM_PATH#";
	private static final String REGISTER_BM_SQL = "sys_register_bm_pkg.register_bm('#HOST_PATH#','#BM_PATH#');";

	private String functionCode;
	private String functionName;
	private String functionOrder;

	private String modulesCode;
	private String modulesName;

	private IFile hostPage;
	private String hostPath;
	private String resultString;

	public RegisterSql(String functionCode, String functionName,
			String functionOrder, String modulesCode, String modulesName,
			IFile hostPage) {
		super();
		this.functionCode = functionCode;
		this.functionName = functionName;
		this.functionOrder = functionOrder;
		this.modulesCode = modulesCode;
		this.modulesName = modulesName;
		this.hostPage = hostPage;
		hostPath = PathUtil.getPathInScreen(hostPage);
		String moduleRegisterSql = MODULE_REGISTER_SQL.replace(MODULE_CODE,
				modulesCode).replace(MODULE_NAME, modulesName);
		functionRegisterSql = functionRegisterSql.append(moduleRegisterSql)
				.append("\n");
		String fRegisterSql = FUNCTION_REGISTER_SQL
				.replace(FUNCTION_CODE, functionCode)
				.replace(FUNCTION_NAME, functionName)
				.replace(MODULE_CODE, modulesCode).replace(HOST_PATH, hostPath)
				.replace(FUNCTION_ORDER, functionOrder);
		functionRegisterSql = functionRegisterSql.append(fRegisterSql).append(
				"\n");

		InputStream resourceAsStream = getClass().getResourceAsStream(
				"functionRegister.sql");
		resultString = FileUtil.readStringFile(resourceAsStream).toString();

	}

	public String build(List<IFile> files) {
		for (IFile f : files) {
			String fileExtension = f.getFileExtension();
			if ("screen".equalsIgnoreCase(fileExtension)
					|| "svc".equalsIgnoreCase(fileExtension)) {
				String pagePath = PathUtil.getPathInScreen(f);

				Comment fileComment = CompositeMapUtil.getFileComment(f);
				String pageName = f.getProjectRelativePath()
						.removeFileExtension().lastSegment();
				if (fileComment != null) {
					Object object = fileComment.get(Comment.PAGE_NAME);
					if (object != null && !"".equals(object))
						pageName = object.toString();
				}
				String pSql = PAGE_REGISTER_SQL.replace(PAGE_PATH, pagePath)
						.replace(PAGE_NAME, pageName);
				pageRegisterSql = pageRegisterSql.append(pSql).append("\n");

				String lSql = LOAD_SERVICE_SQL.replace(HOST_PATH, hostPath)
						.replace(PAGE_PATH, pagePath);
				serviceRegisterSql = serviceRegisterSql.append(lSql).append(
						"\n");
			}
			if ("bm".equalsIgnoreCase(fileExtension)) {
				String bmPath = PathUtil.getPathInScreen(f);
				String sql = REGISTER_BM_SQL.replace(HOST_PATH, hostPath)
						.replace(BM_PATH, bmPath);
				bmRegisterSql = bmRegisterSql.append(sql).append("\n");
			}
		}
		resultString = resultString
				.replace(PAGE_REGISTER, pageRegisterSql.toString())
				.replace(MODULE_REGISTER, functionRegisterSql.toString())
				.replace(LOAD_SERVICE, serviceRegisterSql.toString())
				.replace(REGISTER_BM, bmRegisterSql.toString());
		return resultString;
	}

}
