package uncertain.ide.eclipse.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import uncertain.ide.Activator;
import uncertain.ide.eclipse.editor.core.ISqlViewer;
import uncertain.ide.help.CustomDialog;
import uncertain.ide.help.LocaleMessage;

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

//		String action = viewer.getAction();
		String action = sql.split(" ")[0];
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
			CustomDialog.showErrorMessageBox(e);
			return;
		}
		viewer.refresh(resultSet, resultCount);
	}

	public static ImageDescriptor getDefaultImageDescriptor() {
		return Activator.getImageDescriptor(LocaleMessage.getString("run.icon"));
	}
}
