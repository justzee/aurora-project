package aurora.bpm.command;

import javax.sql.DataSource;

import org.eclipse.bpmn2.Definitions;

import uncertain.composite.CompositeMap;
import aurora.bpm.engine.ExecutorContext;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallEnabled;
import aurora.sqlje.core.ISqlCallStack;
import aurora.sqlje.core.SqlCallStack;

public abstract class AbstractCommandExecutor implements ICommandExecutor {

	public static final String INSTANCE_ID = "instance_id";
	public static final String PROCESS_CODE = "process_code";
	public static final String PROCESS_VERSION = "process_version";

	private ExecutorContext context;
	protected IDatabaseServiceFactory dsf;

	public AbstractCommandExecutor(IDatabaseServiceFactory dsf) {
		this.dsf = dsf;
	}

	public void init(ExecutorContext context) {
		this.context = context;
	}

	public ExecutorContext getExecutorContext() {
		return context;
	}

	protected IDatabaseServiceFactory getDatabaseServiceFactory() {
		return dsf;
	}

	/**
	 * create a new SqlCallStack
	 * 
	 * @return
	 * @throws Exception
	 */
	protected ISqlCallStack createSqlCallStack() throws Exception {
		DataSource ds = getDatabaseServiceFactory().getDataSource();
		ISqlCallStack callStack = new SqlCallStack(ds, ds.getConnection());
		return callStack;
	}

	/**
	 * release a SqlCallStack
	 * 
	 * @param callStack
	 * @throws Exception
	 */
	protected void releaseSqlCallStack(ISqlCallStack callStack)
			throws Exception {
		if (callStack != null)
			callStack.free(callStack.getCurrentConnection());
	}

	@Override
	public void execute(Command cmd) throws Exception {
		ISqlCallStack callStack = createSqlCallStack();
		try {
			executeWithSqlCallStack(callStack, cmd);
		} finally {
			releaseSqlCallStack(callStack);
		}
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {

	}

	protected org.eclipse.bpmn2.Definitions loadDefinitions(String code,
			String version, ISqlCallStack callStack) throws Exception {
		return getExecutorContext().getDefinitionFactory().loadDefinition(code,
				version, callStack);
	}

	protected Definitions loadDefinitions(Command cmd, ISqlCallStack callStack)
			throws Exception {
		return loadDefinitions(cmd.getOptions().getString(PROCESS_CODE), cmd
				.getOptions().getString(PROCESS_VERSION), callStack);
	}

	protected <T extends ISqlCallEnabled> T createProc(Class<T> clazz,
			ISqlCallStack callStack) {
		T t = getExecutorContext().getInstanceManager().createInstance(clazz);
		t._$setSqlCallStack(callStack);
		return t;
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	/**
	 * copy <code>instance_id</code> ,<code>process_code</code>,
	 * <code>process_version</code> from <code>cmd0</code>
	 * 
	 * @param cmd0
	 * @return
	 */
	protected CompositeMap createOptionsWithProcessInfo(Command cmd0) {
		CompositeMap map = new CompositeMap();
		map.put(INSTANCE_ID, cmd0.getOptions().getString(INSTANCE_ID));
		map.put(PROCESS_CODE, cmd0.getOptions().getString(PROCESS_CODE));
		map.put(PROCESS_VERSION, cmd0.getOptions().getString(PROCESS_VERSION));
		return map;
	}

	/**
	 * find CommandExecutor for <code>cmd2</code> ,and execute it with
	 * <code>callStack</code>
	 * 
	 * @param callStack
	 * @param cmd2
	 * @throws Exception
	 */
	protected void dispatchCommand(ISqlCallStack callStack, Command cmd2)
			throws Exception {
		ICommandExecutor executor = getExecutorContext().getCommandRegistry()
				.findExecutor(cmd2);
		executor.executeWithSqlCallStack(callStack, cmd2);
	}

}
