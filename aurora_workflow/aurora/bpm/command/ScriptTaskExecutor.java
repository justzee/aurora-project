package aurora.bpm.command;

import org.eclipse.bpmn2.ScriptTask;

import aurora.bpm.script.BPMScriptEngine;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ScriptTaskExecutor extends AbstractCommandExecutor {
	public ScriptTaskExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
		// TODO Auto-generated constructor stub
	}

	public static final String TYPE = "SCRIPTTASK";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		super.executeWithSqlCallStack(callStack, cmd);
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		ScriptTask st = findFlowElementById(process, node_id, ScriptTask.class);
		String script = st.getScript();
		if (script != null && script.length() > 0) {
			BPMScriptEngine engine = prepareScriptEngine(callStack, cmd);
			engine.registry("process", process);
			engine.registry("currentNode", st);
			engine.eval(script);
		}
		System.out.println("[script task]" + node_id + ", executed.");
		createOutgoingPath(callStack, st, cmd);
	}

}
