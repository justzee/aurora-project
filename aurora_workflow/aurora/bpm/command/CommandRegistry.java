package aurora.bpm.command;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.ParallelGateway;

import uncertain.ocm.IObjectCreator;
import aurora.bpm.engine.ExecutorContext;
import aurora.javascript.Ref;

public class CommandRegistry {
	private HashMap<String, ICommandExecutor> registry = new HashMap<String, ICommandExecutor>();

	private ExecutorContext execCtx;
	private IObjectCreator objCreator;

	public CommandRegistry(IObjectCreator objCreator, ExecutorContext context) {
		super();
		this.objCreator = objCreator;
		this.execCtx = context;
		execCtx.setCommandRegistry(this);
		loadDefaultRegistry();
	}

	public void registry(String commandType, ICommandExecutor cmd) {
		if (cmd.getExecutorContext() == null)
			cmd.init(execCtx);
		registry.put(commandType, cmd);
	}

	public void registry(String commandType,
			Class<? extends ICommandExecutor> cmdClazz) throws Exception {
		ICommandExecutor cmd = (ICommandExecutor) objCreator
				.createInstance(cmdClazz);
		cmd.init(execCtx);
		registry.put(commandType, cmd);
	}

	/**
	 * if the specified type is not registered, the UnknownCommandExecutor will
	 * be returned
	 * 
	 * @param type
	 * @return
	 */
	public ICommandExecutor findExecutor(String type) {
		ICommandExecutor e = registry.get(type);
		if (e == null)
			e = registry.get(UnknownCmdExecutor.TYPE);
		return e;
	}

	/**
	 * @see #findExecutor(String)
	 * @param cmd
	 * @return
	 */
	public ICommandExecutor findExecutor(Command cmd) {
		return findExecutor(cmd.getAction());
	}

	private void loadDefaultRegistry() {// TODO
		try {
			loadDefault_(UnknownCmdExecutor.class);
			loadDefault_(CreateInstanceCmdExecutor.class);
			loadDefault_(ApproveCmdExecutor.class);
			loadDefault_(ProceedCmdExecutor.class);
			loadDefault_(ArriveCmdExecutor.class);
			loadDefault_(ScriptTaskExecutor.class);
			loadDefault_(UserTaskExecutor.class);
			bind(ParallelGateway.class, ParallelGatewayExecutor.class);
			System.out.println("builtin command registry loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadDefault_(Class<? extends ICommandExecutor> clazz)
			throws Exception {
		Field fld = clazz.getField("TYPE");
		if (fld != null) {
			String type = (String) fld.get(null);
			registry(type, clazz);
		}
	}

	private void bind(Class<? extends FlowNode> type,
			Class<? extends ICommandExecutor> exec) throws Exception {
		String name = type.getSimpleName();
		if (name.endsWith("Impl"))
			name = name.substring(0, name.length() - 4);
		name = name.toUpperCase();
		registry(name, exec);
	}

}
