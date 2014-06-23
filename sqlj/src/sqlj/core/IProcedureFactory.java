package sqlj.core;

import sqlj.exception.ProcedureCreateException;

public interface IProcedureFactory {

	IProcedure createProcedure(Class<? extends IProcedure> proClass)
			throws ProcedureCreateException;

	IProcedure createProcedure(IContext context,
			Class<? extends IProcedure> proClass)
			throws ProcedureCreateException;

	IProcedure createProcedure(String procName) throws ProcedureCreateException;

	IProcedure createProcedure(IContext context, String procName)
			throws ProcedureCreateException;
}
