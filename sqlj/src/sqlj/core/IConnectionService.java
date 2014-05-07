package sqlj.core;

import java.sql.Connection;
import java.sql.SQLException;

public interface IConnectionService {
	public Connection getConnection() throws SQLException;
}
