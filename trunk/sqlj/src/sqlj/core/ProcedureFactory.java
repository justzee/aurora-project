package sqlj.core;

import sqlj.exception.ProcedureCreateException;

public class ProcedureFactory implements IProcedureFactory {

	@Override
	public <T extends IProcedure> T  createProcedure(Class<? extends IProcedure> proClass)
			throws ProcedureCreateException {
		IProcedure proc = null;
		try {
			proc = proClass.newInstance();
		} catch (Exception e) {
			throw new ProcedureCreateException(e);
		}
		return (T) proc;
	}

	@Override
	public <T extends IProcedure> T  createProcedure(IContext context,
			Class<? extends IProcedure> proClass)
			throws ProcedureCreateException {
		IProcedure proc = createProcedure(proClass);
		associate(context, proc);
		return (T) proc;
	}

	@Override
	public <T extends IProcedure> T  createProcedure(String procName)
			throws ProcedureCreateException {
		Class clazz = null;
		try {
			clazz = Class.forName(procName);
		} catch (ClassNotFoundException e) {
			throw new ProcedureCreateException("procedure '" + procName
					+ "' not exists.", e);
		}
		if (IProcedure.class.isAssignableFrom(clazz)) {
			return (T) createProcedure(clazz);
		}
		throw new ProcedureCreateException("procedure '" + procName
				+ "'is not illegal.", null);
	}

	@Override
	public <T extends IProcedure> T  createProcedure(IContext context, String procName)
			throws ProcedureCreateException {
		IProcedure proc = createProcedure(procName);
		associate(context, proc);
		return (T) proc;
	}

	public void associate(IContext context, IProcedure proc) {
		proc.__init__(context);
		context.registerProcedure(proc);
	}

}
