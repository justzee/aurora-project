package aurora.bpm.command;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;

import uncertain.composite.CompositeMap;
import aurora.bpm.exception.NodeNotFoundException;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ArriveCmdExecutor extends AbstractCommandExecutor {
	public static final String TYPE = "ARRIVE";

	public ArriveCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		FlowElement fe = findFlowElementById(process, node_id);
		if (fe instanceof FlowNode) {
			String className = fe.getClass().getSimpleName();
			if (className.endsWith("Impl"))
				className = className.substring(0, className.length() - 4);
			className = className.toUpperCase();
			CompositeMap opts = cloneOptions(cmd);// TODO
			// create a command,the action is dynamic
			Command cmd2 = new Command(className, opts);
			dispatchCommand(callStack, cmd2);
		} else
			throw new NodeNotFoundException(node_id, cmd.getOptions()
					.getString(PROCESS_CODE), cmd.getOptions().getString(
					PROCESS_VERSION));

	}
}
