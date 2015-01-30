package aurora.bpm.command.sqlje;

import aurora.sqlje.core.annotation.*;
import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class process_log implements aurora.sqlje.core.ISqlCallEnabled {
	public void log(Long instance_id, Long user_id, String event_type,
			String log_content) throws Exception {
		ProcessLog data = new ProcessLog();
		data.instance_id = instance_id;
		data.user_id = user_id;
		data.event_type = event_type;
		data.log_content = log_content;
		$sql.insert(data);
		commit();
	}

	private final void commit() throws SQLException {
		ISqlCallStack cs = getSqlCallStack();
		cs.commit();
		cs.free(cs.getCurrentConnection(), false);
	}

	public void set_instance_error(Long instance_id) throws Exception {
		String _$sqlje_sql_gen2 = "update bpmn_process_instance set status='ERROR' where instance_id=?";
		PreparedStatement _$sqlje_ps_gen1 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen2);
		_$sqlje_ps_gen1.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen1.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen1.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen1.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen1);
		commit();
	}

	public static void log() {
	}

	@Table(name = "bpmn_process_log", stdwho = false)
	public static class ProcessLog {
		@PK
		public Long log_id;
		public Long instance_id;
		public Long user_id;
		public String event_type;
		public String log_content;
		@InsertExpression("CURRENT_TIMESTAMP")
		public java.sql.Timestamp log_date;
	}

	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}
}
