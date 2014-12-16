package aurora.bpm.engine;

import aurora.bpm.command.CommandRegistry;
import aurora.bpm.model.DefinitionFactory;
import aurora.sqlje.core.IInstanceManager;

public class ExecutorContext {
	private ProcessEngine engine;
	private DefinitionFactory factory;
	private IInstanceManager instManger;
	private CommandRegistry cmdRegistry;

	public ExecutorContext(IInstanceManager instManger,
			DefinitionFactory defFactory) {
		super();
		this.instManger = instManger;
		factory = defFactory;
		System.out.println(getClass().getSimpleName() + " instance created.");
	}

	public ProcessEngine getProcessEngine() {
		return engine;
	}

	public DefinitionFactory getDefinitionFactory() {
		return factory;
	}

	public IInstanceManager getInstanceManager() {
		return instManger;
	}

	public void setCommandRegistry(CommandRegistry cr) {
		this.cmdRegistry = cr;
	}

	public CommandRegistry getCommandRegistry() {
		return cmdRegistry;
	}
}
