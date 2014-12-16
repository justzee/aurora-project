package aurora.bpm.script;

import aurora.javascript.Context;
import aurora.javascript.ContextFactory;
import aurora.javascript.RhinoException;

public class BPMScriptEngine {
	static {
		RhinoException.useMozillaStackStyle(false);
		initTopLevel(Context.enter());
		Context.exit();
		ContextFactory.initGlobal(new ContextFactory() {
			protected Context makeContext() {
				Context cx = super.makeContext();
				cx.setLanguageVersion(Context.VERSION_1_8);
				cx.setOptimizationLevel(-1);
				cx.setClassShutter(RhinoClassShutter.getInstance());
				cx.setWrapFactory(RhinoWrapFactory.getInstance());
				return cx;
			}
		});
	}
	
	static void initTopLevel(Context cx) {
		
	}
	
//	public Object eval(String source) throws Exception {
//		Object ret = null;
//		Context cx = Context.enter();
//		try {
//			cx.putThreadLocal(KEY_SERVICE_CONTEXT, service_context);
//			cx.setOptimizationLevel(optimizeLevel);
//			if (scope == null) {
//				scope = cx.newObject(topLevel);
//				scope.setParentScope(null);
//				scope.setPrototype(topLevel);
//			}
//			preDefine(cx, scope);
//			ScriptImportor.organizeUserImport(cx, scope, service_context);
//			Script scr = CompiledScriptCache.getInstance()
//					.getScript(source, cx);
//			ret = scr == null ? null : scr.exec(cx, scope);
//		} catch (RhinoException re) {
//			if (re.getCause() instanceof InterruptException)
//				throw (InterruptException) re.getCause();
//			throw re;
//		} finally {
//			Context.exit();
//		}
//
//		if (ret instanceof Wrapper) {
//			ret = ((Wrapper) ret).unwrap();
//		} else if (ret instanceof Undefined)
//			ret = null;
//		return ret;
//	}
}
