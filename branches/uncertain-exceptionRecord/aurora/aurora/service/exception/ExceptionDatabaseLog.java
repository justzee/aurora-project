package aurora.service.exception;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.sql.DataSource;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.core.UncertainEngine;
import uncertain.exception.ExceptionNotice;
import uncertain.exception.IExceptionListener;
import uncertain.ocm.IConfigurable;
import uncertain.ocm.IObjectRegistry;
import aurora.service.ServiceThreadLocal;

public class ExceptionDatabaseLog extends DynamicObject implements IExceptionListener,IConfigurable {

	Set ignoredTypes;
	UncertainEngine mUncertainEngine;
	public static final String IGNORED_TYPES = "ignored-types";
	public static final String INSERT_SQL = "insert-sql";
	private String sql;
	private Connection conn;

	public ExceptionDatabaseLog(UncertainEngine engine) {
		ignoredTypes = new HashSet();
		mUncertainEngine = engine;
		add2ExceptionNotice();
	}
	private void prepare() {
		CompositeMap types = object_context.getChild(IGNORED_TYPES);
		if (types != null && types.getChildIterator() != null) {
			for (Iterator it = types.getChildIterator(); it.hasNext();) {
				CompositeMap type = (CompositeMap) it.next();
				if (!"ignored-type".equalsIgnoreCase(type.getName())) {
					throw new ConfigurationError("ignored-types can contain ignored-type element only!");
				}
				String className = type.getString("name");
				if (className == null) {
					throw new ConfigurationError("Must set 'name' property");
				}
				try {
					Class.forName(className);
				} catch (ClassNotFoundException e) {
					throw new ConfigurationError(e);
				}
				if (!ignoredTypes.contains(className))
					ignoredTypes.add(className);
			}
		}
		CompositeMap sqlNode = object_context.getChild(INSERT_SQL);
		if (sqlNode == null) {
			throw new ConfigurationError("Must has " + INSERT_SQL + " node!");
		}
		sql = sqlNode.getText();
		if (sqlNode == null || "".equals(sql)) {
			throw new ConfigurationError("sql can not be empty!");
		}
	}

	public void add2ExceptionNotice() {
		Object exceptionNoticeObject = mUncertainEngine.getObjectRegistry().getInstanceOfType(ExceptionNotice.class);
		if (exceptionNoticeObject != null) {
			((ExceptionNotice) exceptionNoticeObject).addListener(this);
		}
	}

	public DynamicObject initialize(CompositeMap context) {
		super.initialize(context);
		prepare();
		return this;
	}

	public void onException(Throwable exception) {
		if (exception == null)
			return;
		if (ignoredTypes.contains(exception.getClass().getCanonicalName())) {
			return;
		}
		if (conn == null) {
			conn = initConnection(mUncertainEngine.getObjectRegistry());
		}
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, exception.getClass().getCanonicalName());
			ps.setString(2, exception.getMessage());
			StringBuffer sb = new StringBuffer("");
			ExceptionNotice.getPrintStackTrace(sb, exception);
			String stackTrace = sb.toString().length()>2000?sb.toString().substring(0, 2000):sb.toString();
			ps.setString(3, stackTrace);
			ps.setString(4, ServiceThreadLocal.getCurrentThreadContext().toXML());
			ps.executeUpdate();
			ps.close();
		} catch (Throwable e) {
			throw new ConfigurationError(e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					mUncertainEngine.logException("close PreparedStatement failed.", e);
				}
			}
		}

	}

	private Connection initConnection(IObjectRegistry registry) {
		DataSource ds = (DataSource) registry.getInstanceOfType(DataSource.class);
		try {
			if (ds == null)
				throw new RuntimeException("Can not get DataSource from registry " + registry);
			return ds.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("Can not get Connection from DataSource", e);
		}
	}
	public void beginConfigure(CompositeMap config) {
		 initialize(config);
	}
	public void endConfigure() {
	}

}
