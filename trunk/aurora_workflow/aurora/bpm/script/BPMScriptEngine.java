package aurora.bpm.script;

import java.util.HashMap;

import uncertain.composite.CompositeMap;
import aurora.javascript.Context;
import aurora.javascript.RhinoException;
import aurora.javascript.Script;
import aurora.javascript.Scriptable;
import aurora.javascript.ScriptableObject;
import aurora.javascript.Undefined;
import aurora.javascript.Wrapper;
import aurora.plugin.script.engine.AuroraScriptEngine;
import aurora.plugin.script.engine.CompiledScriptCache;
import aurora.plugin.script.engine.InterruptException;

public class BPMScriptEngine extends AuroraScriptEngine {
	private HashMap<String, Object> localVariable = new HashMap<String, Object>();

	public BPMScriptEngine(CompositeMap context) {
		super(context);
	}

	public void registry(String name, Object obj) {
		localVariable.put(name, obj);
	}

	@Override
	protected void preDefine(Context cx, Scriptable scope) {
		super.preDefine(cx, scope);
		for (String key : localVariable.keySet())
			ScriptableObject.defineProperty(scope, key, localVariable.get(key),
					ScriptableObject.READONLY);
	}

	public Object eval(String source) throws Exception {
		Object ret = null;
		Context cx = Context.enter();
		try {
			cx.putThreadLocal(KEY_SERVICE_CONTEXT, service_context);
			Scriptable scope = cx.newObject(topLevel);
			scope.setParentScope(null);
			scope.setPrototype(topLevel);
			preDefine(cx, scope);
			//ScriptImportor.organizeUserImport(cx, scope, service_context);
			Script scr = CompiledScriptCache.getInstance()
					.getScript(source, cx);
			ret = scr == null ? null : scr.exec(cx, scope);
		} catch (RhinoException re) {
			if (re.getCause() instanceof InterruptException)
				throw (InterruptException) re.getCause();
			throw re;
		} finally {
			Context.exit();
		}

		if (ret instanceof Wrapper) {
			ret = ((Wrapper) ret).unwrap();
		} else if (ret instanceof Undefined)
			ret = null;
		return ret;
	}

}