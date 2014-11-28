package aurora.bpm.command;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.Process;

import uncertain.composite.CompositeMap;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ArriveCmdExecutor extends AbstractCommandExecutor {
	public ArriveCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "ARRIVE";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString("node_id");
		Definitions def = loadDefinitions(cmd, callStack);
		org.eclipse.bpmn2.Process process = (Process) def.eContents().get(0);
		for (FlowElement fe : process.getFlowElements()) {
			if (fe instanceof FlowNode && eq(node_id, fe.getId())) {
				String className = fe.getClass().getSimpleName();
				if (className.endsWith("Impl"))
					className = className.substring(0, className.length() - 4);
				className = className.toUpperCase();
				CompositeMap opts = createOptionsWithProcessInfo(cmd);// TODO
																		// fill
																		// some
																		// info

				// create a command,the action is dynamic
				Command cmd2 = new Command(className, opts);
				dispatchCommand(callStack, cmd2);
				break;
			}
		}

	}
}
