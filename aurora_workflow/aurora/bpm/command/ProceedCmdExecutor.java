package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.SequenceFlow;

import uncertain.composite.CompositeMap;
import aurora.bpm.command.sqlje.BpmnPathInstance;
import aurora.bpm.command.sqlje.path;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ProceedCmdExecutor extends AbstractCommandExecutor {
	public ProceedCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "PROCEED";

	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		path cp = createProc(path.class, callStack);
		Long instance_id = cmd.getOptions().getLong("instance_id");
		Long path_id = cmd.getOptions().getLong("path_id");
		BpmnPathInstance bpi = cp.query(path_id);
		String preNode = bpi.prev_node;
		String curNode = bpi.current_node;
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		List<FlowElement> eles = process.getFlowElements();
		for (FlowElement ef : eles) {
			if (ef instanceof SequenceFlow) {
				SequenceFlow sf = (SequenceFlow) ef;
				if (eq(preNode, sf.getSourceRef().getId())
						&& eq(curNode, sf.getTargetRef().getId())) {
					// path found
					cp.close(instance_id, path_id);
					CompositeMap opts = createOptionsWithStandardInfo(cmd);
					opts.put(NODE_ID, sf.getTargetRef().getId());

					// create a ARRIVE command
					Command cmd2 = new Command(ArriveCmdExecutor.TYPE, opts);
					dispatchCommand(callStack, cmd2);

					break;
				}
			}
		}

	}

}
