package aurora.bpm.command;

import org.eclipse.bpmn2.UserTask;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ApproveCmdExecutor extends AbstractCommandExecutor {
	public ApproveCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "APPROVE";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		UserTask ut = findFlowElementById(process, node_id, UserTask.class);
		System.out.println("user task:" + node_id + " APPROVED.");
		createOutgoingPath(callStack, ut, cmd);
	}

}
