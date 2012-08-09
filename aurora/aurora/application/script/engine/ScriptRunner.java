package aurora.application.script.engine;

import javax.script.ScriptException;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.application.script.scriptobject.ScriptShareObject;

public class ScriptRunner {
	private String exp;
	private AuroraScriptEngine engine;

	private CompositeMap context = null;
	private ScriptShareObject sso;

	public ScriptRunner(String script) {
		this.exp = script;

	}

	public ScriptRunner(String script, CompositeMap context) {
		this(script);
		this.context = context;
		sso = (ScriptShareObject) context.get(AuroraScriptEngine.KEY_SSO);
		if (sso == null) {
			sso = new ScriptShareObject();
			context.put(AuroraScriptEngine.KEY_SSO, sso);
		}
	}

	public ScriptRunner(String script, CompositeMap context,
			IObjectRegistry registry) {
		this(script, context);
		sso.put(registry);
	}

	public String getOriginalScript() {
		return exp;
	}

	public void setImport(String import1) {
		sso.put(ScriptShareObject.KEY_IMPORT, import1);
	}

	public String getImport() {
		return sso.get(ScriptShareObject.KEY_IMPORT);
	}

	public String getParsedScript() {
		if (context == null)
			return exp;
		return TextParser.parse(exp, context);
	}

	public Object run() throws ScriptException {
		engine = sso.getEngine();
		if (engine == null) {
			engine = new AuroraScriptEngine(context);
			sso.put(engine);
		}
		String str = getParsedScript();
		return engine.eval(str);
	}

	public void setProcedureRunner(ProcedureRunner runner) {
		sso.put(ScriptShareObject.KEY_RUNNER, runner);
	}
}