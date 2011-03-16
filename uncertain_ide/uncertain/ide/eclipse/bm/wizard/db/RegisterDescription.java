package uncertain.ide.eclipse.bm.wizard.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import oracle.jdbc.driver.OracleConnection;
import uncertain.ide.help.ApplicationException;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.SystemException;

public class RegisterDescription {
	public static final String SYS_PROMPTS_TABLE = "sys_prompts";
	public static int CREATED_BY = 1;
	public static String CREATION_DATE = "sysdate";
	public static String LANGUAGE = "ZHS";
	private HashMap promptList = new HashMap();
	private Connection connection;
	public RegisterDescription(Connection connection) {
		this.connection = connection;
	}

	public void addPrompt(String prompt_code, String description) {
		promptList.put(prompt_code, description);
	}
	public HashMap getPromptList() {
		return promptList;
	}

	public void setPromptList(HashMap promptList) {
		this.promptList = promptList;
	}

	public void run() throws ApplicationException {
		if (promptList.keySet() == null)
			return;
		String selectSQL = "select 1 from sys_prompts t where t.prompt_code=? and t.language=?";
		String insertSQL = " insert into sys_prompts(prompt_id,prompt_code,language,description,created_by,creation_date,last_updated_by,last_update_date)values(sys_prompts_s.nextval,?,?,?,?,sysdate,?,sysdate)";
		if (!(connection instanceof OracleConnection)) {
			CustomDialog.showErrorMessageBox("注册描述功能暂时只支持Oracle数据库");
		}
		Set keySet = promptList.keySet();
		PreparedStatement queryPS = null;
		PreparedStatement insertPS = null;
		try {
			connection.setAutoCommit(false);
			queryPS = connection.prepareStatement(selectSQL);
			insertPS = connection.prepareStatement(insertSQL);
			for (Iterator it = keySet.iterator(); it.hasNext();) {
				String prompt_code = (String) it.next();
				String description = (String) promptList.get(prompt_code);
				queryPS.setString(1, prompt_code);
				queryPS.setString(2, LANGUAGE);
				if (!isNone(queryPS.executeQuery()))
					continue;
				insertPS.setString(1, prompt_code);
				insertPS.setString(2, LANGUAGE);
				insertPS.setString(3, description);
				insertPS.setInt(4, CREATED_BY);
				insertPS.setInt(5, CREATED_BY);
				insertPS.executeUpdate();
			}
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new SystemException(e);
		} finally {
			try {
				if (queryPS != null)
					queryPS.close();
				if (insertPS != null)
					insertPS.close();
			} catch (SQLException e) {
				CustomDialog.showErrorMessageBox(e);
			}
		}
	}
	public boolean isNone(ResultSet set) throws SystemException {
		if (set == null)
			return true;
		try {
			if (!set.next())
				return true;
		} catch (SQLException e) {
			throw new SystemException(e);
		}
		return false;
	}
}
