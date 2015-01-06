package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.SequenceFlow;

import aurora.bpm.command.sqlje.gateway;
import aurora.bpm.exception.NodeNotFoundException;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ParallelGatewayExecutor extends AbstractCommandExecutor {

	public ParallelGatewayExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		ParallelGateway pg = findFlowElementById(process, node_id,
				ParallelGateway.class);
		if (pg == null) {
			throw new NodeNotFoundException(node_id, cmd.getOptions()
					.getString(PROCESS_CODE), cmd.getOptions().getString(
					PROCESS_VERSION));
		}
		List<SequenceFlow> list = pg.getIncoming();
		if (pg.getOutgoing().size() > 1) {
			gateway gw = createProc(gateway.class, callStack);
			Long arrived_count = gw.update_arrived(
					cmd.getOptions().getLong(INSTANCE_ID), node_id);
			System.out.printf("[parallel gateway]%s arrived: %d/%d\n",
					node_id, arrived_count, list.size());
			if (arrived_count < list.size())
				return;
		}
		System.out.println("[parallel gateway]" + node_id + " start dispatching");
		createOutgoingPath(callStack, pg, cmd);
		System.out.println("[parallel gateway]" + node_id + " end   dispatching");

	}
}
