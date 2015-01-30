package aurora.bpm.command.sqlje;

import java.util.List;
import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class get_employee_unit_manager extends recipient_rule_common implements
		IRecipientRule, aurora.sqlje.core.ISqlCallEnabled {
	public void execute(String param1, String param2, String param3,
			String param4, Long rule_record_id) throws Exception {
		String _$sqlje_sql_gen4 = "select * from bpmn_instance_node_rule where rule_record_id=?";
		PreparedStatement _$sqlje_ps_gen3 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen4);
		_$sqlje_ps_gen3.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen3.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen3.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen3.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen3);
		BpmnInstanceNodeRule node_rule = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen0);
		Long company_id = get_instance_company(node_rule.instance_id);
		List<Long> positions = get_document_position(node_rule.instance_id);
		if (positions.size() == 0)
			return;
		for (Long position_id : positions) {
			String _$sqlje_sql_gen6 = "SELECT n.chief_position_id\n\t                FROM wfl_position_v p, wfl_unit_v n\n\t               WHERE p.position_id = ?\n\t                 AND p.unit_id = n.unit_id";
			PreparedStatement _$sqlje_ps_gen5 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen6);
			_$sqlje_ps_gen5.setLong(1, position_id);
			$sql.clear();
			_$sqlje_ps_gen5.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen5.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen5.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen5);
			for (Long chief_position_id : new ResultSetIterator<Long>(
					_$sqlje_rs_gen1, Long.class)) {
				String _$sqlje_sql_gen8 = "SELECT DISTINCT u.user_id\n\t                    FROM wfl_employee_assigns_v a, wfl_users_v u\n\t                   WHERE a.position_id = ?\n\t                     AND a.employee_id = u.employee_id\n\t                     AND a.company_id = ?";
				PreparedStatement _$sqlje_ps_gen7 = getSqlCallStack()
						.getCurrentConnection().prepareStatement(
								_$sqlje_sql_gen8);
				_$sqlje_ps_gen7.setLong(1, chief_position_id);
				_$sqlje_ps_gen7.setLong(2, company_id);
				$sql.clear();
				_$sqlje_ps_gen7.execute();
				$sql.UPDATECOUNT = _$sqlje_ps_gen7.getUpdateCount();
				ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen7.getResultSet();
				getSqlCallStack().push(_$sqlje_rs_gen2);
				getSqlCallStack().push(_$sqlje_ps_gen7);
				for (Long approver_id : new ResultSetIterator<Long>(
						_$sqlje_rs_gen2, Long.class)) {
					insert_wfl_instance_node_hirc(node_rule.recipient_sequence,
							approver_id, "", rule_record_id, null, 1L, null);
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
