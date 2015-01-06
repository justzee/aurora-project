package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.io.Reader;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class gateway implements aurora.sqlje.core.ISqlCallEnabled {
	/**
	 * called when parallel_gateway arrived.
	 */
	public Long update_arrived(Long instance_id, String node_id)
			throws Exception {
		String _$sqlje_sql_gen3 = "update bpmn_parallel_gateway_status \n\t\t\t set arrived_count = arrived_count + 1\n\t\t   where instance_id = ?\n\t\t\tand  node_id     = ?;\n\t\t";
		PreparedStatement _$sqlje_ps_gen2 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen3);
		_$sqlje_ps_gen2.setLong(1, instance_id);
		_$sqlje_ps_gen2.setString(2, node_id);
		$sql.clear();
		_$sqlje_ps_gen2.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen2.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen2.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen2);
		if ($sql.rowcount() == 0) {
			CompositeMap data = new CompositeMap();
			data.put("instance_id", instance_id);
			data.put("node_id", node_id);
			data.put("arrived_count", 1);
			$sql.insert(data, "bpmn_parallel_gateway_status", "status_id");
		}
		String _$sqlje_sql_gen5 = "select arrived_count from bpmn_parallel_gateway_status where instance_id=? and node_id=?";
		PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen5);
		_$sqlje_ps_gen4.setLong(1, instance_id);
		_$sqlje_ps_gen4.setString(2, node_id);
		$sql.clear();
		_$sqlje_ps_gen4.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen4.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen4);
		Long arrived = DataTransfer.transfer1(Long.class, _$sqlje_rs_gen1);
		return arrived;
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag();

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
		$sql.setInstanceManager(_$sqlje_instanceManager);
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
		$sql.setSqlCallStack(_$sqlje_sqlCallStack);
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
