package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.util.List;
import aurora.bpm.command.sqlje.*;
import aurora.bpm.command.beans.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class PathProc implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(Long instance_id, String preNode, String currentNode,
			String node_id) throws Exception {
		BpmnPathInstance bpi = new BpmnPathInstance();
		bpi.instance_id = instance_id;
		bpi.status = "ACTIVE";
		bpi.prev_node = preNode;
		bpi.current_node = currentNode;
		bpi.node_id = node_id;
		$sql.insert(bpi);
		BpmnProcessToken token = new BpmnProcessToken();
		token.instance_id = instance_id;
		token.path_id = bpi.path_id;
		token.node_id = node_id;
		$sql.insert(token);
		return bpi.path_id;
	}

	public void createPathLog(Long instance_id, Long path_id, Long user_id,
			String prev_node, String current_node, String log_content)
			throws Exception {
		BpmnPathLog bpl = new BpmnPathLog();
		bpl.instance_id = instance_id;
		bpl.path_id = path_id;
		bpl.user_id = "" + user_id;
		bpl.prev_node = prev_node;
		bpl.current_node = current_node;
		bpl.log_content = bpl.log_content;
		$sql.insert(bpl);
	}

	public void closePath(Long instance_id, Long path_id) throws Exception {
		String _$sqlje_sql_gen6 = "update bpmn_path_instance\n\t\t\t set status='CLOSED'\n\t\t   where instance_id=? \n\t\t\t and path_id=?";
		PreparedStatement _$sqlje_ps_gen5 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen6);
		_$sqlje_ps_gen5.setLong(1, instance_id);
		_$sqlje_ps_gen5.setLong(2, path_id);
		$sql.clear();
		_$sqlje_ps_gen5.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen5.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen5.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen5);
	}

	public BpmnProcessToken getToken(Long instance_id, String sf_id)
			throws Exception {
		try {
			String _$sqlje_sql_gen8 = "select * from bpmn_process_token \nwhere instance_id=? and node_id = ? order by token_id";
			_$sqlje_sql_gen8 = $sql._$prepareLimitSql(_$sqlje_sql_gen8);
			PreparedStatement _$sqlje_ps_gen7 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen8);
			_$sqlje_ps_gen7.setLong(1, instance_id);
			_$sqlje_ps_gen7.setString(2, sf_id);
			$sql._$prepareLimitParaBinding(_$sqlje_ps_gen7, 0, 1, 3);
			$sql.clear();
			_$sqlje_ps_gen7.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen7.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen7.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen7);
			BpmnProcessToken token = DataTransfer.transfer1(
					BpmnProcessToken.class, _$sqlje_rs_gen1);
			return token;
		} catch (NoDataFoundException e) {
		}
		return null;
	}

	public List<BpmnProcessToken> getTokens(Long instance_id, String sf_id)
			throws Exception {
		String _$sqlje_sql_gen10 = "select * from bpmn_process_token \nwhere instance_id=? and node_id = ? order by token_id";
		PreparedStatement _$sqlje_ps_gen9 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen10);
		_$sqlje_ps_gen9.setLong(1, instance_id);
		_$sqlje_ps_gen9.setString(2, sf_id);
		$sql.clear();
		_$sqlje_ps_gen9.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen9.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen9.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen9);
		List<BpmnProcessToken> list = DataTransfer.transferAll(List.class,
				BpmnProcessToken.class, _$sqlje_rs_gen2);
		return list;
	}

	/**
	 * consume a token and close related path
	 */
	public void consumeToken(BpmnProcessToken token) throws Exception {
		$sql.delete(token);
		closePath(token.instance_id, token.path_id);
	}

	public BpmnPathInstance query(Long path_id) throws Exception {
		String _$sqlje_sql_gen12 = "select * from bpmn_path_instance where path_id=?";
		PreparedStatement _$sqlje_ps_gen11 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen12);
		_$sqlje_ps_gen11.setLong(1, path_id);
		$sql.clear();
		_$sqlje_ps_gen11.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen11.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen11.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen11);
		BpmnPathInstance bpi = DataTransfer.transfer1(BpmnPathInstance.class,
				_$sqlje_rs_gen3);
		return bpi;
	}

	public void setCurrent(Long path_id, String nextNode) throws Exception {
		String _$sqlje_sql_gen14 = "update bpmn_path_instance \n\t\t\t set current_node = ?\n\t\t   where path_id = ?\n\t\t";
		PreparedStatement _$sqlje_ps_gen13 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen14);
		_$sqlje_ps_gen13.setString(1, nextNode);
		_$sqlje_ps_gen13.setLong(2, path_id);
		$sql.clear();
		_$sqlje_ps_gen13.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen13.getUpdateCount();
		ResultSet _$sqlje_rs_gen4 = _$sqlje_ps_gen13.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen4);
		getSqlCallStack().push(_$sqlje_ps_gen13);
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
