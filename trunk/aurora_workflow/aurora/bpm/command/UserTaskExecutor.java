package aurora.bpm.command;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class UserTaskExecutor extends AbstractCommandExecutor {
	public UserTaskExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "USERTASK";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		
	}

}
