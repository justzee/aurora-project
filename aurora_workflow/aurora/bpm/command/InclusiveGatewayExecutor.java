package aurora.bpm.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.SequenceFlow;

import aurora.bpm.script.BPMScriptEngine;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class InclusiveGatewayExecutor extends AbstractCommandExecutor {

	public InclusiveGatewayExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		InclusiveGateway ig = findFlowElementById(process, node_id,
				InclusiveGateway.class);
		BPMScriptEngine engine = prepareScriptEngine(callStack, cmd);
		engine.registry("process", process);
		engine.registry("currentNode", ig);
		List<SequenceFlow> list = ig.getOutgoing();
		List<SequenceFlow> passedSf = new ArrayList<SequenceFlow>();
		for (SequenceFlow sf : list) {
			if (sf == ig.getDefault())
				continue;
			FormalExpression exp = (FormalExpression) sf
					.getConditionExpression();
			String body = exp == null ? null : exp.getBody();
			if (body == null || body.length() == 0) {
				System.out
						.printf("[inclusive gateway]%s,%s,condition is empty,always true\n",
								node_id, sf.getId());
				passedSf.add(sf);
				continue;
			}
			Object ret = engine.eval(body);
			System.out.printf(
					"[inclusive gateway]%s,%s,condition execution result:%s\n",
					node_id, sf.getId(), "" + ret);
			if (ret instanceof Boolean && ((Boolean) ret).booleanValue()) {
				passedSf.add(sf);
			}
		}
		if (passedSf.size() == 0) {
			SequenceFlow defaultSf = ig.getDefault();
			if (defaultSf == null) {
				throw new RuntimeException("no default sequence flow specified");
			}
			passedSf.add(defaultSf);
		}
		for (SequenceFlow sf : passedSf) {
			createPath(callStack, sf, cmd);
		}

	}

}
