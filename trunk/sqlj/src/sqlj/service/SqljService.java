package sqlj.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

import sqlj.core.*;
import sqlj.exception.MethodNotDeclaredException;
import sqlj.exception.ProcedureCreateException;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.IProcedureManager;
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
			getResponse().setContentType("application/json");
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
			getContextMap().createChild("error").put("error", thr.getMessage());
			getResponse().getWriter().print(
					"{\"success\":false,error:\""
							+ thr.getMessage().replace("\"", "\\\"") + "\"}");
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
	public IProcedure getProcedure(Class<? extends IProcedure> procClass)
			throws ProcedureCreateException {
		return getProcedureFactory().createProcedure(this, procClass);
	}

	@Override
	public IProcedure getProcedure(String procName)
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

}