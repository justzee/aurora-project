package sqlj.core;

import java.sql.Connection;

public abstract class Procedure {

	private IConnectionService connService;

	/**
	 * to indicate ,how many rows are updated during last execution
	 */
	protected int UPDATE_COUNT;

	public void setConnectionService(IConnectionService connServ) {
		this.connService = connServ;
	}

	protected Connection getConnection() throws Exception {
		return connService.getConnection();
	}

	public abstract void execute(IContextService context) throws Exception;
}
