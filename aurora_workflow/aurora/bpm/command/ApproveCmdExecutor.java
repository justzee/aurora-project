package aurora.bpm.command;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ApproveCmdExecutor extends AbstractCommandExecutor {
	public ApproveCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
		// TODO Auto-generated constructor stub
	}

	public static final String TYPE="APPROVE";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		// TODO Auto-generated method stub
		
	}


}
