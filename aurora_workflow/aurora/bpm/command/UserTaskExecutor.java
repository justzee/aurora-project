package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.UserTask;

import uncertain.composite.CompositeMap;
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
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		UserTask ut = findFlowElementById(process, node_id, UserTask.class);
		List<ResourceRole> resRoles=ut.getResources();
		for(ResourceRole rl:resRoles) {
			//rl.get
		}
		//ut.get
		System.out.println("[user task]" + node_id + ", recipient created.");
		// test code
		CompositeMap opts = cloneOptions(cmd);
		Command cmd2 = new Command(ApproveCmdExecutor.TYPE, opts);
		dispatchCommand(callStack, cmd2);
		// end test code
	}

}
