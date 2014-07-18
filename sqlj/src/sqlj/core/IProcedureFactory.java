package sqlj.core;

import sqlj.exception.ProcedureCreateException;

public interface IProcedureFactory {

	<T extends IProcedure> T  createProcedure(Class<? extends IProcedure> proClass)
			throws ProcedureCreateException;

	<T extends IProcedure> T  createProcedure(IContext context,
			Class<? extends IProcedure> proClass)
			throws ProcedureCreateException;

	<T extends IProcedure> T  createProcedure(String procName) throws ProcedureCreateException;

	<T extends IProcedure> T  createProcedure(IContext context, String procName)
			throws ProcedureCreateException;
}
