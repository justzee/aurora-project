package aurora.bpm.command;

import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.FormalExpression;

import aurora.bpm.command.sqlje.gateway;
import aurora.bpm.script.BPMScriptEngine;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ComplexGatewayExecutor extends AbstractCommandExecutor {

	public ComplexGatewayExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		super.executeWithSqlCallStack(callStack, cmd);
		Long instance_id = cmd.getOptions().getLong(INSTANCE_ID);
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));

		gateway gw = createProc(gateway.class, callStack);
		boolean wait_for_start = gw.isWaitingForStart(instance_id, node_id);
		if (!wait_for_start) {
			System.out.println("[complex gateway]" + node_id
					+ " is not waiting for start.");
			return;
		}
		ComplexGateway cg = findFlowElementById(process, node_id,
				ComplexGateway.class);
		BPMScriptEngine engine = prepareScriptEngine(callStack, cmd);
		engine.registry("process", process);
		engine.registry("currentNode", cg);

		FormalExpression exp = (FormalExpression) cg.getActivationCondition();
	}

}
