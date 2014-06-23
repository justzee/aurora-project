package sqlj.core;

import java.sql.*;
import java.util.Collection;

import javax.sql.DataSource;

import sqlj.exception.ProcedureCreateException;

public interface IContext {
	Connection getConnection() throws SQLException;
	Connection getConnection(String name) throws SQLException;
	DataSource getDataSource();
	void setDataSource(DataSource dataSource);
	
	<T> T getContextObject();
	
	Collection<String> getAttributeNames();
	Object getAttribute(String name);
	void setAttribute(String name,Object value);
	
	void setProcedureFactory(IProcedureFactory procFactory);
	IProcedureFactory getProcedureFactory();
	
	/**
	 * procedure will be create by <i>procFactory</i>,and associate with this IContext
	 * @param procClass
	 * @return
	 * @throws ProcedureCreateException 
	 * @see IProcedureFactory#createProcedure(IContext, Class)
	 */
	IProcedure getProcedure(Class<? extends IProcedure> procClass) throws ProcedureCreateException;
	/**
	 * @param procName
	 * @return
	 * @throws ProcedureCreateException 
	 * @see #getProcedure(Class)
	 */
	IProcedure getProcedure(String procName) throws ProcedureCreateException;
	
	
	void registerProcedure(IProcedure proc);
	void registerResultSet(ResultSet rs);
	void registerStatement(Statement ps);
	/**
	 * clean procedures and resultsets associate with this IContext
	 */
	void clean();
}
