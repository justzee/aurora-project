package aurora.ide.prompt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PromptManager {
	public static final String SQL = "select  language, description  from sys_prompts t where t.prompt_code=?";

	public static String[] getPrompts(String code, Connection conn) {
		String[] result = new String[2];
		PreparedStatement sta = null;
		if (conn != null) {
			try {
				sta = conn.prepareStatement(SQL);
				sta.setString(1, code);
				ResultSet resultSet = sta.executeQuery();
				while (resultSet.next()) {
					String language = resultSet.getString("language");
					String description = resultSet.getString("description");
					// ZHS / US
					if ("ZHS".equalsIgnoreCase(language)) {
						result[0] = description;
					}
					if ("US".equalsIgnoreCase(language)) {
						result[1] = description;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (sta != null) {
					try {
						sta.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return result;
	}

}
