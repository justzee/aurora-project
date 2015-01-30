package aurora.bpm.engine;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;
import aurora.bpm.command.CommandRegistry;
import aurora.bpm.model.DefinitionFactory;
import aurora.bpm.script.BPMScriptEngine;
import aurora.plugin.script.engine.AuroraScriptEngine;
import aurora.plugin.script.scriptobject.ScriptShareObject;
import aurora.sqlje.core.IInstanceManager;

public class ExecutorContext {
	private DefinitionFactory factory;
	private IInstanceManager instManger;
	private CommandRegistry cmdRegistry;
	private IObjectRegistry ior;

	public ExecutorContext(IInstanceManager instManger,
			DefinitionFactory defFactory, IObjectRegistry ior) {
		super();
		this.instManger = instManger;
		factory = defFactory;
		this.ior = ior;
		System.out.println(getClass().getSimpleName() + " instance created.");
	}

	public void destory() {
		
	}


	public IObjectRegistry getObjectRegistry() {
		return ior;
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
			sso.put(getObjectRegistry());
			context.put(AuroraScriptEngine.KEY_SSO, sso);

		}
		BPMScriptEngine engine = new BPMScriptEngine(context);
		sso.put(engine);
		engine.registry("executorContext", this);
		engine.registry("instanceManager", instManger);
		return engine;
	}
}
