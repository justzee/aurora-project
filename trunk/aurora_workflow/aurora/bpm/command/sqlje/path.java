package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.io.Reader;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class path implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(Long instance_id, String preNode, String currentNode)
			throws Exception {
		CompositeMap data = new CompositeMap();
		data.put("instance_id", instance_id);
		data.put("status", "RUNNING");
		data.put("prev_node", preNode);
		data.put("current_node", currentNode);
		new aurora.sqlje.core.database.MysqlInsert(getSqlCallStack(), data,
				"bpmn_path_instance", "path_id").insert();
		return data.getLong("path_id");
	}

	public void close(Long instance_id, Long path_id) throws Exception {
		PreparedStatement _$sqlje_ps_gen3 = getSqlCallStack()
				.getCurrentConnection()
				.prepareStatement(
						"update bpm_path_instance\n\t\t\t set status='FINISH'\n\t\t   where instance_id=? \n\t\t\t and path_id=?\n\t\t");
		_$sqlje_ps_gen3.setLong(1, instance_id);
		_$sqlje_ps_gen3.setLong(2, path_id);
		_$sqlje_ps_gen3.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen3.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen3.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen3);
	}

	public CompositeMap query(Long path_id) throws Exception {
		PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(
						"select * from bm_path_instance where path_id=?");
		_$sqlje_ps_gen4.setLong(1, path_id);
		_$sqlje_ps_gen4.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen4.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen4);
		CompositeMap data = DataTransfer.transfer1(CompositeMap.class,
				_$sqlje_rs_gen1);
		return data;
	}

	public void setCurrent(Long path_id, String nextNode) throws Exception {
		PreparedStatement _$sqlje_ps_gen5 = getSqlCallStack()
				.getCurrentConnection()
				.prepareStatement(
						"update bpm_path_instance \n\t\t\t set current_node = ?\n\t\t   where path_id = ?\n\t\t");
		_$sqlje_ps_gen5.setString(1, nextNode);
		_$sqlje_ps_gen5.setLong(2, path_id);
		_$sqlje_ps_gen5.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen5.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen5.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen5);
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag();

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
