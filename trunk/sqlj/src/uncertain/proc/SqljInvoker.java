package uncertain.proc;

import java.sql.Connection;
import java.sql.SQLException;

import sqlj.core.IConnectionService;
import sqlj.core.IContextService;
import sqlj.exception.UserDefinedException;
import sqlj.exception.UserDefinedExceptionDescriptor;
import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.TextParser;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.ocm.IObjectRegistry;
import aurora.database.service.BusinessModelServiceContext;
import aurora.database.service.SqlServiceContext;
import aurora.service.ServiceContext;
import aurora.service.exception.ExceptionDescriptorConfig;
import aurora.service.exception.IExceptionDescriptor;

public class SqljInvoker extends AbstractEntry implements IConnectionService {

	String proc;
	private SqlServiceContext serviceContext;
	private IObjectRegistry reg;

	public SqljInvoker(IObjectRegistry reg) {
		this.reg = reg;
		registerExceptionDescriptor();
	}

	private void registerExceptionDescriptor() {
		if (UserDefinedExceptionDescriptor.registed)
			return;
		ExceptionDescriptorConfig excpDesc = (ExceptionDescriptorConfig) reg
				.getInstanceOfType(IExceptionDescriptor.class);
		CompositeMap item = new CompositeMap(
				UserDefinedException.class.getSimpleName());
		item.put("exception", UserDefinedException.class.getName());
		item.put("handleclass", UserDefinedExceptionDescriptor.class.getName());
		item.put("xmlns", UserDefinedExceptionDescriptor.class.getPackage()
				.getName());
		try {
			excpDesc.addExceptionDescriptor(item);
			UserDefinedExceptionDescriptor.registed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(ProcedureRunner runner) throws Exception {
		if (proc == null) {
			BuiltinExceptionFactory.createAttributeMissing(this, "proc");
		}
		final CompositeMap ctx = runner.getContext();
		proc = TextParser.parse(proc, ctx);
		Class<?> clazz = Class.forName(proc);
		if (!sqlj.core.Procedure.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException(proc + " is not instanceof "
					+ sqlj.core.Procedure.class.getName());
		}
		serviceContext = (SqlServiceContext) DynamicObject.cast(ctx,
				BusinessModelServiceContext.class);
		sqlj.core.Procedure procedure = (sqlj.core.Procedure) clazz
				.newInstance();
		procedure.setConnectionService(this);
		procedure.execute(new IContextService() {

			@Override
			public Object getContext() {
				return ServiceContext.createServiceContext(ctx);
			}
		});
	}

	public String getProc() {
		return proc;
	}

	public void setProc(String proc) {
		this.proc = proc;
	}

	@Override
	public Connection getConnection() throws SQLException {
		serviceContext.initConnection(reg, null);
		return serviceContext.getConnection();
	}

}
