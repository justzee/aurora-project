package aurora.bpm.command;

import aurora.bpm.command.sqlje.InstanceProc;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class EndEventExecutor extends AbstractCommandExecutor {
	

	public EndEventExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		System.out.println("[End Event]"+cmd.getOptions().getString("node_id")+"  reached");
		Long instance_id = cmd.getOptions()
				.getLong(INSTANCE_ID);
		InstanceProc inst = createProc(InstanceProc.class, callStack);
		inst.finish(instance_id);
	}

}
