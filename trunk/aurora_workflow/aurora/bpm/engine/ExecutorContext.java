package aurora.bpm.engine;

import uncertain.composite.CompositeMap;
import aurora.bpm.command.CommandRegistry;
import aurora.bpm.model.DefinitionFactory;
import aurora.bpm.script.BPMScriptEngine;
import aurora.plugin.script.engine.AuroraScriptEngine;
import aurora.plugin.script.scriptobject.ScriptShareObject;
import aurora.sqlje.core.IInstanceManager;
import aurora.sqlje.core.InstanceManager;

public class ExecutorContext {
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

	public BPMScriptEngine createScriptEngine(CompositeMap context) {
		ScriptShareObject sso = (ScriptShareObject) context
				.get(AuroraScriptEngine.KEY_SSO);
		if (sso == null) {
			sso = new ScriptShareObject();
			sso.put(((InstanceManager) instManger).getObjectRegistry());
			context.put(AuroraScriptEngine.KEY_SSO, sso);

		}
		BPMScriptEngine engine = new BPMScriptEngine(context);
		sso.put(engine);
		engine.registry("executorContext", this);
		engine.registry("instanceManager", instManger);
		return engine;
	}
}
