package sqlj.core;

import java.sql.*;

public class DbUtil {
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager
				.getConnection(
						"jdbc:oracle:thin:@192.168.11.65:1521:masdev","hec2dev","hec2dev");
	}
	
	public static Connection getConnection2() throws SQLException{
		return DriverManager
				.getConnection(
						"jdbc:mysql://172.20.0.38:3306/masdev","hap_dev","hap_dev");
	}

	public static void freeConnection(Connection conn) {
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
}
