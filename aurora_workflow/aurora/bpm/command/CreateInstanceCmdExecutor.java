package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.StartEvent;

import uncertain.composite.CompositeMap;
import aurora.bpm.command.sqlje.instance;
import aurora.bpm.command.sqlje.path;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class CreateInstanceCmdExecutor extends AbstractCommandExecutor {
	public static final String TYPE = "CREATE";

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

		instance ci = createProc(instance.class, callStack);
		Long instance_id = ci.create(options.getString(PROCESS_CODE), version);
		cmd.getOptions().put(INSTANCE_ID, instance_id);// set new instance_id back
		System.out.println("instance created ,id:" + instance_id);
		for (FlowElement fe : process.getFlowElements()) {
			if (fe instanceof StartEvent) {
				System.out.println("find start event:" + fe);
				StartEvent se = (StartEvent) fe;
				List<SequenceFlow> outgoing = se.getOutgoing();
				for (SequenceFlow sf : outgoing) {
					System.out.println("\toutgoing:" + sf);
					FlowNode target = sf.getTargetRef();
					System.out.println(target);
					// create path
					createPath(callStack, sf, cmd);
				}
			}
		}
	}

}
