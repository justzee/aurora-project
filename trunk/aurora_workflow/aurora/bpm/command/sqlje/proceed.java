package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.io.Reader;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class proceed implements aurora.sqlje.core.ISqlCallEnabled {
	public void proceed(Long instance_id, Long path_id) throws Exception {
		PreparedStatement _$sqlje_ps_gen2 = getSqlCallStack()
				.getCurrentConnection()
				.prepareStatement(
						"SELECT * FROM bpm_path_instance WHERE instance_id=? and path_id=? FOR UPDATE");
		_$sqlje_ps_gen2.setLong(1, instance_id);
		_$sqlje_ps_gen2.setLong(2, path_id);
		_$sqlje_ps_gen2.execute();
		getSqlCallStack().push(_$sqlje_ps_gen2);
		PreparedStatement _$sqlje_ps_gen3 = getSqlCallStack()
				.getCurrentConnection()
				.prepareStatement(
						"select * from bpm_path_instance where instance_id=? and path_id=?");
		_$sqlje_ps_gen3.setLong(1, instance_id);
		_$sqlje_ps_gen3.setLong(2, path_id);
		_$sqlje_ps_gen3.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen3.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen3.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen3);
		CompositeMap path = DataTransfer.transfer1(CompositeMap.class,
				_$sqlje_rs_gen0);
		if ("RUNNING".equals(path.getString("status"))) {
			PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
					.getCurrentConnection()
					.prepareStatement(
							"select * from bpm_process_instance where instance_id=?");
			_$sqlje_ps_gen4.setLong(1, instance_id);
			_$sqlje_ps_gen4.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen4.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen4);
			CompositeMap instance = DataTransfer.transfer1(CompositeMap.class,
					_$sqlje_rs_gen1);
		}
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag();

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
