package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.SequenceFlow;

import aurora.bpm.script.BPMScriptEngine;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ExclusiveGatewayExecutor extends AbstractCommandExecutor {

	public ExclusiveGatewayExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		ExclusiveGateway eg = findFlowElementById(process, node_id,
				ExclusiveGateway.class);
		List<SequenceFlow> outgoings = eg.getOutgoing();
		SequenceFlow decision = null;
		BPMScriptEngine engine = prepareScriptEngine(callStack, cmd);
		engine.registry("process", process);
		engine.registry("currentNode", eg);
		for (SequenceFlow sf : outgoings) {
			if (sf == eg.getDefault())
				continue;
			FormalExpression exp = (FormalExpression) sf
					.getConditionExpression();
			String body = exp == null ? null : exp.getBody();
			if (body == null || body.length() == 0) {
				System.out
						.printf("[exclusive gateway]%s,%s,condition is empty,always true\n",
								node_id, sf.getId());
				decision = sf;
				break;
			}
			Object ret = engine.eval(body);
			System.out.printf(
					"[exclusive gateway]%s,%s,condition execution result:%s\n",
					node_id, sf.getId(), "" + ret);
			if (ret instanceof Boolean && ((Boolean) ret).booleanValue()) {
				decision = sf;
				break;
			}
		}
		if (decision == null) {
			System.out
					.printf("[exclusive gateway]%s,no conditions test success,try default..\n",
							node_id);
			decision = eg.getDefault();
		}

		if (decision == null) {
			System.out
					.printf("[exclusive gateway]%s,no default sequence flow specified ,throw exception..\n",
							node_id);
			throw new RuntimeException("no default sequence flow specified");// TODO
		}
		System.out.printf("[exclusive gateway]%s,decision found,%s\n", node_id,
				decision.getId());

		createPath(callStack, decision, cmd);

	}

}
