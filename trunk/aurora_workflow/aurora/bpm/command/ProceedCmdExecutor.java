package aurora.bpm.command;

import org.eclipse.bpmn2.SequenceFlow;

import uncertain.composite.CompositeMap;
import aurora.bpm.command.beans.BpmnPathInstance;
import aurora.bpm.command.sqlje.PathProc;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

/**
 * internal command executor
 * @author jessen
 *
 */
public class ProceedCmdExecutor extends AbstractCommandExecutor {
	public ProceedCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "PROCEED";

	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		PathProc cp = createProc(PathProc.class, callStack);
		Long instance_id = cmd.getOptions().getLong(INSTANCE_ID);
		Long path_id = cmd.getOptions().getLong(PATH_ID);
		BpmnPathInstance bpi = cp.query(path_id);
		
		
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		SequenceFlow sf = findFlowElementById(process, bpi.node_id,
				SequenceFlow.class);

		//cp.close(instance_id, path_id); path will close when the target node accept it.
		CompositeMap opts = createOptionsWithStandardInfo(cmd);
		opts.put(SEQUENCE_FLOW_ID, cmd.getOptions().getString(SEQUENCE_FLOW_ID));
		opts.put(NODE_ID, sf.getTargetRef().getId());

		// create a ARRIVE command
		Command cmd2 = new Command(ArriveCmdExecutor.TYPE, opts);
		dispatchCommand(callStack, cmd2);

	}

}
