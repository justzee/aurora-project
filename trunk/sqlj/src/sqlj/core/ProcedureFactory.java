package sqlj.core;

import sqlj.exception.ProcedureCreateException;

public class ProcedureFactory implements IProcedureFactory {

	@Override
	public IProcedure createProcedure(Class<? extends IProcedure> proClass)
			throws ProcedureCreateException {
		IProcedure proc = null;
		try {
			proc = proClass.newInstance();
		} catch (Exception e) {
			throw new ProcedureCreateException(e);
		}
		return proc;
	}

	@Override
	public IProcedure createProcedure(IContext context,
			Class<? extends IProcedure> proClass)
			throws ProcedureCreateException {
		IProcedure proc = createProcedure(proClass);
		associate(context, proc);
		return proc;
	}

	@Override
	public IProcedure createProcedure(String procName)
			throws ProcedureCreateException {
		Class clazz = null;
		try {
			clazz = Class.forName(procName);
		} catch (ClassNotFoundException e) {
			throw new ProcedureCreateException("procedure '" + procName
					+ "' not exists.", e);
		}
		if (IProcedure.class.isAssignableFrom(clazz)) {
			return createProcedure(clazz);
		}
		throw new ProcedureCreateException("procedure '" + procName
				+ "'is not illegal.", null);
	}

	@Override
	public IProcedure createProcedure(IContext context, String procName)
			throws ProcedureCreateException {
		IProcedure proc = createProcedure(procName);
		associate(context, proc);
		return proc;
	}

	public void associate(IContext context, IProcedure proc) {
		proc.__init__(context);
		context.registerProcedure(proc);
	}

}
