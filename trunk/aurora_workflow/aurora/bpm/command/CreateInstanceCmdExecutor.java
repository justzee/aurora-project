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
		org.eclipse.bpmn2.Definitions def = loadDefinitions(cmd, callStack);
		org.eclipse.bpmn2.Process process = (org.eclipse.bpmn2.Process) def
				.eContents().get(0);

		instance ci = createProc(instance.class, callStack);
		Long instance_id = ci.create(options.getString(PROCESS_CODE), version);
		System.out.println("instance created ,id:" + instance_id);
		for (FlowElement fe : process.getFlowElements()) {
			if (fe instanceof StartEvent) {
				System.out.println("find start event:" + fe);
				StartEvent se = (StartEvent) fe;
				List<SequenceFlow> outgoing = se.getOutgoing();
				path cp = createProc(path.class, callStack);
				for (SequenceFlow path : outgoing) {
					System.out.println("\toutgoing:" + path);
					FlowNode target = path.getTargetRef();
					System.out.println(target);
					// create path
					Long path_id = cp.create(instance_id, se.getId(),
							target.getId());
					System.out.println("path created ,id:" + path_id);

					CompositeMap opts = createOptionsWithProcessInfo(cmd);
					opts.put(INSTANCE_ID, instance_id);
					opts.put("path_id", path_id);
					// create a PROCEED command
					Command cmd2 = new Command(ProceedCmdExecutor.TYPE, opts);
					dispatchCommand(callStack, cmd2);
				}
			}
		}
	}

}
