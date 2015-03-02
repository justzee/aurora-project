package aurora.bpm.command;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.StartEvent;

import uncertain.composite.CompositeMap;
import aurora.bpm.command.sqlje.InstanceProc;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class CreateInstanceCmdExecutor extends AbstractCommandExecutor {
	public static final String TYPE = "CREATE";// Start Event

	public CreateInstanceCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		CompositeMap options = cmd.getOptions();

		String version = options.getString(PROCESS_VERSION);
		org.eclipse.bpmn2.Process process = (org.eclipse.bpmn2.Process) getProcess(loadDefinitions(
				cmd, callStack));
		Long parent_id = options.getLong("parent_id");
		Long instance_param = options.getLong("instance_param");
		InstanceProc ci = createProc(InstanceProc.class, callStack);
		Long instance_id = ci.create(options.getString(PROCESS_CODE), version,
				parent_id,instance_param);
		cmd.getOptions().put(INSTANCE_ID, instance_id);// set new instance_id
														// back
		System.out.println("instance created ,id:" + instance_id);
		loadDataObject(ci, instance_id, callStack);
		for (FlowElement fe : process.getFlowElements()) {
			if (fe instanceof StartEvent) {
				System.out.println("find start event:" + fe);
				createOutgoingPath(callStack, (StartEvent) fe, cmd);
			}
		}
	}

}
