package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class get_employee extends recipient_rule_common implements
		IRecipientRule, aurora.sqlje.core.ISqlCallEnabled {
	public void execute(String param1, String param2, String param3,
			String param4, Long rule_record_id) throws Exception {
		String _$sqlje_sql_gen3 = "select * from bpmn_instance_node_rule where rule_record_id = ?";
		PreparedStatement _$sqlje_ps_gen2 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen3);
		_$sqlje_ps_gen2.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen2.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen2.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen2.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen2);
		BpmnInstanceNodeRule node_rule = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen0);
		List<Long> emps = get_document_employee(node_rule.instance_id);
		if (emps.size() == 0)
			return;
		for (Long document_employee_id : emps) {
			String _$sqlje_sql_gen5 = "select distinct u.user_id\n\t\t\t\t\t\t\t\t\tfrom wfl_users_v u\n\t\t\t\t\t\t\t\t\twhere u.employee_id=?";
			PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen5);
			_$sqlje_ps_gen4.setLong(1, document_employee_id);
			$sql.clear();
			_$sqlje_ps_gen4.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen4.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen4);
			for (Long approver_id : new ResultSetIterator<Long>(
					_$sqlje_rs_gen1, Long.class)) {
				insert_wfl_instance_node_hirc(node_rule.recipient_sequence,
						approver_id, "", rule_record_id, null, 1L, null);
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
