package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class proceed implements aurora.sqlje.core.ISqlCallEnabled {
	public void proceed(Long instance_id, Long path_id) throws Exception {
		String _$sqlje_sql_gen3 = "SELECT * FROM bpm_path_instance WHERE instance_id=? and path_id=?";
		_$sqlje_sql_gen3 = $sql._$prepareLockSql("bpm_path_instance",
				"instance_id=${instance_id} and path_id=${path_id}");
		PreparedStatement _$sqlje_ps_gen2 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen3);
		_$sqlje_ps_gen2.setLong(1, instance_id);
		_$sqlje_ps_gen2.setLong(2, path_id);
		$sql.clear();
		_$sqlje_ps_gen2.execute();
		getSqlCallStack().push(_$sqlje_ps_gen2);
		String _$sqlje_sql_gen5 = "select * from bpm_path_instance where instance_id=? and path_id=?";
		PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen5);
		_$sqlje_ps_gen4.setLong(1, instance_id);
		_$sqlje_ps_gen4.setLong(2, path_id);
		$sql.clear();
		_$sqlje_ps_gen4.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen4.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen4);
		CompositeMap path = DataTransfer.transfer1(CompositeMap.class,
				_$sqlje_rs_gen0);
		if ("RUNNING".equals(path.getString("status"))) {
			String _$sqlje_sql_gen7 = "select * from bpm_process_instance where instance_id=?";
			PreparedStatement _$sqlje_ps_gen6 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen7);
			_$sqlje_ps_gen6.setLong(1, instance_id);
			$sql.clear();
			_$sqlje_ps_gen6.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen6.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen6.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen6);
			CompositeMap instance = DataTransfer.transfer1(CompositeMap.class,
					_$sqlje_rs_gen1);
		}
	}

	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}
}
