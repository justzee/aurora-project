package aurora.ide.bm.editor.toolbar.action;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import aurora.ide.AuroraPlugin;
import aurora.ide.editor.core.ISqlViewer;
import aurora.ide.helpers.DialogUtil;
import aurora.ide.helpers.LocaleMessage;



public class ExecuteSqlAction extends Action {
	ISqlViewer viewer;

	public ExecuteSqlAction(ISqlViewer viewer) {
		this.viewer = viewer;
	}

	public ExecuteSqlAction(ISqlViewer viewer,ImageDescriptor imageDescriptor, String text) {
		if (imageDescriptor != null)
			setHoverImageDescriptor(imageDescriptor);
		if (text != null)
			setText(text);
		this.viewer = viewer;
	}

	public void run() {
		Connection conn = viewer.getConnection();
		String sql = viewer.getSql();
		if(sql == null ||"".equals(sql)){
			DialogUtil.showErrorMessageBox("请先输入SQL语句。");
			return;
		}
		String action = sql.trim().split(" ")[0];
		ResultSet resultSet = null;
		Statement stmt;
		int resultCount = 0;
		try {
			stmt = conn.createStatement();
			if ("select".equalsIgnoreCase(action)) {
				resultSet = stmt.executeQuery(sql);
			}
			else if(action != null){
				resultCount = stmt.executeUpdate(sql);
			}
			if(resultSet != null){
				resultCount = resultSet.getFetchSize();
			}
		} catch (SQLException e) {
			DialogUtil.showExceptionMessageBox(e);
			return;
		}
		viewer.refresh(resultSet, resultCount);
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return AuroraPlugin.getImageDescriptor(LocaleMessage.getString("run.icon"));
	}
}
