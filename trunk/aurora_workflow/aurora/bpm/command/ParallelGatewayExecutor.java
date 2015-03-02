package aurora.bpm.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.ParallelGateway;
import org.eclipse.bpmn2.SequenceFlow;

import aurora.bpm.command.beans.BpmnProcessToken;
import aurora.bpm.command.sqlje.PathProc;
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
		Long instance_id = cmd.getOptions().getLong(INSTANCE_ID);
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
		PathProc p = createProc(PathProc.class, callStack);
		List<BpmnProcessToken> tokens = new ArrayList<BpmnProcessToken>(
				list.size());
		// check each incoming sequence flow has a token
		for (SequenceFlow sf : list) {
			BpmnProcessToken token = p.getToken(instance_id, sf.getId());
			if (token == null)
				continue;//break
			//use continue,we can know how many incoming sequence flow has arrived 
			tokens.add(token);
		}

		System.out.printf("[parallel gateway]%s arrived: %d/%d (current arrived:%s)\n", node_id,
				tokens.size(), list.size(),cmd.getOptions().getString(SEQUENCE_FLOW_ID));
		if (tokens.size() < list.size())
			return;

		// consume a token on each incoming sequence flow
		for (BpmnProcessToken t : tokens)
			p.consumeToken(t);

		System.out.println("[parallel gateway]" + node_id
				+ " start dispatching");
		createOutgoingPath(callStack, pg, cmd);
		System.out.println("[parallel gateway]" + node_id
				+ " end   dispatching");

	}
}
