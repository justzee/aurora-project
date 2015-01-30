package aurora.bpm.command;

import java.io.ByteArrayInputStream;

import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.StartEvent;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import aurora.bpm.command.sqlje.BpmnProcessData;
import aurora.bpm.command.sqlje.instance;
import aurora.bpm.script.BPMScriptEngine;
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
		instance ci = createProc(instance.class, callStack);
		Long instance_id = ci.create(options.getString(PROCESS_CODE), version,
				parent_id,instance_param);
		cmd.getOptions().put(INSTANCE_ID, instance_id);// set new instance_id
														// back
		System.out.println("instance created ,id:" + instance_id);
		//prepare data_object($data)
		BpmnProcessData data = ci.getProcessData(instance_id);
		if (data != null) {
			CompositeMap map;
			if (data.data_object == null || data.data_object.length() == 0)
				map = new CompositeMap();
			else {
				JSONObject dataMap = new JSONObject(data.data_object);
				map = JSONAdaptor.toMap(dataMap);
			}
			map.setName(BPMScriptEngine.DATA_OBJECT);
			// put data_object to context(if it not exists)
			callStack.getContextData().addChild(map);
		}
		//
		for (FlowElement fe : process.getFlowElements()) {
			if (fe instanceof StartEvent) {
				System.out.println("find start event:" + fe);
				createOutgoingPath(callStack, (StartEvent) fe, cmd);
			}
		}
	}

}
