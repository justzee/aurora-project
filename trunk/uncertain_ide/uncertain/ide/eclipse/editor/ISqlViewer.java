/**
 * 
 */
package uncertain.ide.eclipse.editor;

import java.sql.Connection;
import java.sql.ResultSet;


public interface ISqlViewer{
	public Connection  getConnection();
	public String  getSql();
	public String  getAction();
	public void refresh(ResultSet resultSet,int resultCount);
}
