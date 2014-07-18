package sqlj.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import sqlj.core.*;
import sqlj.core.database.DatabaseDescriptor;
import sqlj.core.database.IDatabaseDescriptor;
import sqlj.core.database.MysqlInsert;
import sqlj.core.database.OracleInsert;
import sqlj.exception.MethodNotDeclaredException;
import sqlj.exception.ProcedureCreateException;
import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
import uncertain.proc.ProcedureRunner;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;
import aurora.service.ServiceController;
import aurora.service.http.HttpServiceInstance;
import aurora.service.json.JSONPrintWriter;
import aurora.service.json.JSONServiceInterpreter;

public class SqljService extends HttpServiceInstance implements IContext {
	private String procName;
	private String methodName;

	private IProcedureFactory procFactory;

	private Set<IProcedure> registedProcedues = new HashSet<IProcedure>();
	private List<Object> registedResultSets = new ArrayList<Object>();

	private SqlServiceContext sqlServiceContext;
	private IObjectRegistry objectRegistry;
	IProcedure proc;
	private boolean outputManual = false;
	private IDatabaseDescriptor databaseDescriptor;

	public SqljService(String name, IProcedureManager proc_manager) {
		super(name, proc_manager);
		ServiceController.createServiceController(getContextMap())
				.setContinueFlag(false);
		CompositeMap svcConfig = new CompositeMap("virtual-sqlj-config");
		CompositeMap svcoutput = svcConfig.createChild("service-output");
		svcoutput.put("output", "/parameter");
		setServiceConfigData(svcConfig);
	}

	@Override
	public boolean invoke(uncertain.proc.Procedure proc1) throws Exception {
		try {
			databaseDescriptor = new DatabaseDescriptor();
			((DatabaseDescriptor) databaseDescriptor).init(getConnection()
					.getMetaData());
			getResponse().setContentType(
					JSONServiceInterpreter.DEFAULT_JSON_CONTENT_TYPE);
			proc = getProcedure(procName);
			Method m = getMethod();
			if (m == null)
				throw new MethodNotDeclaredException(proc, methodName);
			m.invoke(proc, getServiceContext().getParameter());
		} catch (Exception e) {
			e.printStackTrace();
			Throwable thr = e;
			while (thr.getCause() != null)
				thr = thr.getCause();
			ProcedureRunner runner = new ProcedureRunner();
			runner.setContext(getContextMap());
			CompositeMap error = getContextMap().createChild("error");
			error.put("code", thr.getClass().getName());
			if (thr instanceof SQLException) {
				error.put("errorcode", ((SQLException) thr).getErrorCode());
			}
			error.put("message", thr.getMessage());
			JSONServiceInterpreter intp = new JSONServiceInterpreter();
			intp.onCreateFailResponse(runner);
			return false;
		}
		if (!outputManual) {
			JSONServiceInterpreter intp = new JSONServiceInterpreter();
			intp.onCreateSuccessResponse(sqlServiceContext);
		}
		return true;
	}

	/**
	 * set the output method flag.<br>
	 * by default ,output is processed by the engine automatically. If user want
	 * to do output manually,this flag should be set to true,thus ,the engine
	 * won't do that again(this will avoid some error).
	 * 
	 * @param m
	 */
	public void setOutputManual(boolean m) {
		this.outputManual = m;
	}

	@Override
	public SqlServiceContext getServiceContext() {
		return sqlServiceContext;
	}

	@Override
	public void setContextMap(CompositeMap map) {
		super.setContextMap(map);
		this.sqlServiceContext = SqlServiceContext.createSqlServiceContext(map);
	}

	private Method getMethod() throws Exception {
		for (Method m : proc.getClass().getMethods()) {
			if (m.getModifiers() != Modifier.PUBLIC)
				continue;
			if (m.getName().equals(methodName)) {
				// TODO
				return m;
			}
		}
		return null;
	}

	public IDatabaseDescriptor getDatabaseDescriptor() {
		return databaseDescriptor;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(null);
	}

	@Override
	public Connection getConnection(String name) throws SQLException {
		sqlServiceContext.initConnection(objectRegistry, name);
		return sqlServiceContext.getConnection();
	}

	@Override
	public DataSource getDataSource() {
		return (DataSource) sqlServiceContext
				.getInstanceOfType(DataSource.class);
	}

	@Override
	public void setDataSource(DataSource dataSource) {

	}

	@Override
	public <T> T getContextObject() {
		return (T) sqlServiceContext;
	}

	@Override
	public Collection<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProcedureFactory(IProcedureFactory procFactory) {
		this.procFactory = procFactory;
	}

	@Override
	public IProcedureFactory getProcedureFactory() {
		return procFactory;
	}

	@Override
	public <T extends IProcedure> T getProcedure(
			Class<? extends IProcedure> procClass)
			throws ProcedureCreateException {
		return getProcedureFactory().createProcedure(this, procClass);
	}

	@Override
	public <T extends IProcedure> T getProcedure(String procName)
			throws ProcedureCreateException {
		return getProcedureFactory().createProcedure(this, procName);
	}

	@Override
	public void registerProcedure(IProcedure proc) {
		registedProcedues.add(proc);
	}

	@Override
	public void registerResultSet(ResultSet rs) {
		registedResultSets.add(rs);
	}

	@Override
	public void clean() {
		cleanResultSet();
		cleanProcedure();
		clear();
	}

	private void cleanResultSet() {
		for (Object rs : registedResultSets) {
			try {
				if (rs instanceof ResultSet) {
					((ResultSet) rs).close();
				} else if (rs instanceof Statement) {
					((Statement) rs).close();
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
		registedResultSets.clear();
	}

	private void cleanProcedure() {
		for (IProcedure p : registedProcedues) {
			if (p != null)
				p.__finallize__();
		}
		registedProcedues.clear();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getProcName() {
		return procName;
	}

	public void setProcName(String procName) {
		this.procName = procName;
	}

	public void setObjectRegistry(IObjectRegistry objectRegistry) {
		this.objectRegistry = objectRegistry;
	}

	@Override
	public void registerStatement(Statement ps) {
		registedResultSets.add(ps);
	}

	public Integer getSessionInt(String key) {
		return getServiceContext().getSession().getInt(key);
	}

	public Long getSessionLong(String key) {
		return getServiceContext().getSession().getLong(key);
	}

	public String getSessionString(String key) {
		return getServiceContext().getSession().getString(key);
	}

	public Integer getParameterInt(String key) {
		return getServiceContext().getParameter().getInt(key);
	}

	public Long getParameterLong(String key) {
		return getServiceContext().getParameter().getLong(key);
	}

	public String getParameterString(String key) {
		return getServiceContext().getParameter().getString(key);
	}

	@Override
	public void insert(Object bean) throws SQLException, Exception {
		if (databaseDescriptor.isOracle())
			new OracleInsert(this, bean).insert();
		else if (databaseDescriptor.isMysql())
			new MysqlInsert(this, bean).insert();
	}

	@Override
	public void insert(Map map, String tableName, String pkName)
			throws SQLException, Exception {
		if (databaseDescriptor.isOracle())
			new OracleInsert(this, map, tableName, pkName).insert();
		else if (databaseDescriptor.isMysql())
			new MysqlInsert(this, map, tableName, pkName).insert();
	}

	@Override
	public void update(Object bean) throws SQLException, Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Map map, String tableName, String pkName)
			throws SQLException, Exception {
		// TODO Auto-generated method stub

	}

}