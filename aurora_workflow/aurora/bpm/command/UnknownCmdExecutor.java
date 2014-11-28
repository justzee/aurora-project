package aurora.bpm.command;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class UnknownCmdExecutor extends AbstractCommandExecutor {
	public UnknownCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "UNKNOWN";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String msg = "An unknown command received," + cmd;
		System.err.println(msg);
	}

}
