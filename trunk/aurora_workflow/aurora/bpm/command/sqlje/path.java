package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import aurora.bpm.command.sqlje.*;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class path implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(Long instance_id, String preNode, String currentNode)
			throws Exception {
		BpmnPathInstance bpi = new BpmnPathInstance();
		bpi.instance_id = instance_id;
		bpi.status = "RUNNING";
		bpi.prev_node = preNode;
		bpi.current_node = currentNode;
		$sql.insert(bpi);
		return bpi.path_id;
	}

	public void close(Long instance_id, Long path_id) throws Exception {
		String _$sqlje_sql_gen4 = "update bpmn_path_instance\n\t\t\t set status='FINISH'\n\t\t   where instance_id=? \n\t\t\t and path_id=?\n\t\t";
		PreparedStatement _$sqlje_ps_gen3 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen4);
		_$sqlje_ps_gen3.setLong(1, instance_id);
		_$sqlje_ps_gen3.setLong(2, path_id);
		$sql.clear();
		_$sqlje_ps_gen3.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen3.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen3.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen3);
	}

	public BpmnPathInstance query(Long path_id) throws Exception {
		String _$sqlje_sql_gen6 = "select * from bpmn_path_instance where path_id=?";
		PreparedStatement _$sqlje_ps_gen5 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen6);
		_$sqlje_ps_gen5.setLong(1, path_id);
		$sql.clear();
		_$sqlje_ps_gen5.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen5.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen5.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen5);
		BpmnPathInstance bpi = DataTransfer.transfer1(BpmnPathInstance.class,
				_$sqlje_rs_gen1);
		return bpi;
	}

	public void setCurrent(Long path_id, String nextNode) throws Exception {
		String _$sqlje_sql_gen8 = "update bpmn_path_instance \n\t\t\t set current_node = ?\n\t\t   where path_id = ?\n\t\t";
		PreparedStatement _$sqlje_ps_gen7 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen8);
		_$sqlje_ps_gen7.setString(1, nextNode);
		_$sqlje_ps_gen7.setLong(2, path_id);
		$sql.clear();
		_$sqlje_ps_gen7.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen7.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen7.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen7);
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag();

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}

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
}
