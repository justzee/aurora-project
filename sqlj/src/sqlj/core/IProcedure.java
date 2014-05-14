package sqlj.core;

import java.sql.Connection;

public interface IProcedure {
	void setConnection(Connection conn);

	Connection getConnection();

	void setContext(Object context);

	Object getContext();

	void execute() throws Exception;

	void cleanUp();
}
