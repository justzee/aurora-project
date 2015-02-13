package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class get_employee_grandmanager extends recipient_rule_common implements
		IRecipientRule, aurora.sqlje.core.ISqlCallEnabled {
	public void execute(String param1, String param2, String param3,
			String param4, Long rule_record_id) throws Exception {
		String _$sqlje_sql_gen5 = "select * from bpmn_instance_node_rule where rule_record_id=?";
		PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen5);
		_$sqlje_ps_gen4.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen4.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen4.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen4);
		BpmnInstanceNodeRule node_rule = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen0);
		Long company_id = get_instance_company(node_rule.instance_id);
		List<Long> positions = get_document_position(node_rule.instance_id);
		if (positions.size() == 0)
			return;
		for (Long position_id : positions) {
			String _$sqlje_sql_gen7 = "select parent_position_id\n\t\t\t\tfrom wfl_position_v\n\t\t\t\twhere position_id = ?";
			PreparedStatement _$sqlje_ps_gen6 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen7);
			_$sqlje_ps_gen6.setLong(1, position_id);
			$sql.clear();
			_$sqlje_ps_gen6.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen6.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen6.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen6);
			for (Long parent_position : new ResultSetIterator<Long>(
					_$sqlje_rs_gen1, Long.class)) {
				String _$sqlje_sql_gen9 = "select parent_position_id\n\t\t\t\t\t\tfrom wfl_position_v\n\t\t\t\t\t\twhere position_id = ?";
				PreparedStatement _$sqlje_ps_gen8 = getSqlCallStack()
						.getCurrentConnection().prepareStatement(
								_$sqlje_sql_gen9);
				_$sqlje_ps_gen8.setLong(1, parent_position);
				$sql.clear();
				_$sqlje_ps_gen8.execute();
				$sql.UPDATECOUNT = _$sqlje_ps_gen8.getUpdateCount();
				ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen8.getResultSet();
				getSqlCallStack().push(_$sqlje_rs_gen2);
				getSqlCallStack().push(_$sqlje_ps_gen8);
				for (Long grandparent_position : new ResultSetIterator<Long>(
						_$sqlje_rs_gen2, Long.class)) {
					String _$sqlje_sql_gen11 = "SELECT DISTINCT u.user_id\n                            FROM wfl_employee_assigns_v a, wfl_users_v u\n                           WHERE a.position_id = ?\n                             AND a.employee_id = u.employee_id\n                             AND a.company_id = ?";
					PreparedStatement _$sqlje_ps_gen10 = getSqlCallStack()
							.getCurrentConnection().prepareStatement(
									_$sqlje_sql_gen11);
					_$sqlje_ps_gen10.setLong(1, grandparent_position);
					_$sqlje_ps_gen10.setLong(2, company_id);
					$sql.clear();
					_$sqlje_ps_gen10.execute();
					$sql.UPDATECOUNT = _$sqlje_ps_gen10.getUpdateCount();
					ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen10.getResultSet();
					getSqlCallStack().push(_$sqlje_rs_gen3);
					getSqlCallStack().push(_$sqlje_ps_gen10);
					for (Long approver_id : new ResultSetIterator<Long>(
							_$sqlje_rs_gen3, Long.class)) {
						insert_wfl_instance_node_hirc(
								node_rule.recipient_sequence, approver_id, "",
								rule_record_id, null, 1L, null);
					}
				}
			}
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
