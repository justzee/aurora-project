package aurora.service.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.exception.IExceptionListener;
import uncertain.exception.IExceptionWithContext;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceThreadLocal;

public class ExceptionDatabaseLog implements IExceptionListener,IGlobalInstance {

	private IgnoredType[] ignoredTypes;
	private UncertainEngine mUncertainEngine;
	private String sql;
	private CompositeMap sqlNode;
	public ExceptionDatabaseLog(UncertainEngine engine) {
		mUncertainEngine = engine;
	}
	public void addIgnoredTypes(IgnoredType[] ignoredTypes){
		this.ignoredTypes = ignoredTypes;
	}
	public void setInsertSql(CompositeMap sqlNode){
		this.sqlNode = sqlNode;
		if (sqlNode == null) {
			throw new ConfigurationError("Must has insert-sql node!");
		}
		sql = sqlNode.getText();
		if (sqlNode == null || "".equals(sql)) {
			throw new ConfigurationError("sql can not be empty!");
		}
	}
	public CompositeMap getInsertSql(){
		return sqlNode;
	}

	public void onException(Throwable exception) {
		if (exception == null)
			return;
		if(ignoredTypes != null){
			for(int i=0;i<ignoredTypes.length;i++){
				if (exception.getClass().getCanonicalName().equals(ignoredTypes[i].getName())) {
					return;
				}
			}
		}
		DatabaseServiceFactory databasefactory = (DatabaseServiceFactory) mUncertainEngine.getObjectRegistry().getInstanceOfType(DatabaseServiceFactory.class);
		SqlServiceContext ssc = null;
		PreparedStatement ps = null;
		try {
			ssc = databasefactory.createContextWithConnection();
			Connection conn = ssc.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, exception.getClass().getCanonicalName());
			ps.setString(2, exception.getMessage());
			ps.setString(3, getSource()); 
			String context = getContext(exception);
			ps.setString(4, context);
			String rootStackTrace = getRootStackTrace(exception);
			ps.setString(5, rootStackTrace);
			String fullStackTrace = getFullStackTrace(exception);
			ps.setString(6, fullStackTrace);
			ps.executeUpdate();
			ps.close();
		} catch (Throwable e) {
			throw new ConfigurationError(e);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if(ssc != null){
					ssc.freeConnection();
				}
			} catch (SQLException e) {
				mUncertainEngine.logException("close DataBase resource failed.", e);
			}
		}
	}
	private String getSource(){
		if(ServiceThreadLocal.getSource()!= null)
			return ServiceThreadLocal.getSource();
		return "";
	}
	private String getContext(Throwable exception){
		CompositeMap context;
		if(exception instanceof IExceptionWithContext ){
			IExceptionWithContext e = (IExceptionWithContext)exception;
			context = e.getExceptionContext();
			if(context!= null){
				return context.toXML();
			}
		}
		context = ServiceThreadLocal.getCurrentThreadContext();
		if(context!= null)
			return context.toXML();
		return null;
	}
	private String getRootStackTrace(Throwable exception){
		Throwable rootCause = getRootCause(exception);
		return getExceptionStackTrace(rootCause);
		
	}
	private String getFullStackTrace(Throwable exception){
		return getExceptionStackTrace(exception);
		
	}
	private String getExceptionStackTrace(Throwable exception){
		if(exception == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pw = new PrintStream(baos);
		exception.printStackTrace(pw);
		pw.close();
		return baos.toString();
		
	}
	private Throwable getRootCause(Throwable exception){
		if(exception == null)
			return exception;
		Throwable cause = exception.getCause();
		if(cause == null)
			return exception;
		return getRootCause(cause);
	}
}
