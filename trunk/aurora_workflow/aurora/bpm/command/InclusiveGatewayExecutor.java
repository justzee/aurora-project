package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.SequenceFlow;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class InclusiveGatewayExecutor extends AbstractCommandExecutor {

	public InclusiveGatewayExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString("node_id");
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		for (FlowElement fe : process.getFlowElements()) {
			if (fe instanceof ParallelGateway && eq(node_id, fe.getId())) {
				InclusiveGateway ig = (InclusiveGateway) fe;
				List<SequenceFlow> list = ig.getIncoming();
				
				break;
			}
		}
	}

}
