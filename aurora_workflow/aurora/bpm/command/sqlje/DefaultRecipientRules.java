package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.util.List;
import aurora.bpm.command.beans.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class DefaultRecipientRules implements aurora.sqlje.core.ISqlCallEnabled {
	public BpmnDocumentReference getDocumentReference(Long instance_id)
			throws Exception {
		String _$sqlje_sql_gen17 = "select *\n\t\t\t  from bpmn_document_reference\n\t\t\t  where category_id = (select d.category_id\n\t\t\t                         from bpmn_process_define d, bpmn_process_instance i\n\t\t\t                        where d.process_code = i.process_code\n\t\t\t                          and d.process_version = i.process_version\n\t\t\t                          and i.instance_id = ?)\n\t\t";
		PreparedStatement _$sqlje_ps_gen16 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen17);
		_$sqlje_ps_gen16.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen16.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen16.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen16.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen16);
		BpmnDocumentReference doc_ref = DataTransfer.transfer1(
				BpmnDocumentReference.class, _$sqlje_rs_gen0);
		return doc_ref;
	}

	/**
	 * 取得单据ID
	 */
	public Long getInstanceParam(Long instance_id) throws Exception {
		String _$sqlje_sql_gen19 = "select instance_param from bpmn_process_instance where instance_id=?";
		PreparedStatement _$sqlje_ps_gen18 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen19);
		_$sqlje_ps_gen18.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen18.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen18.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen18.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen18);
		Long instance_param = DataTransfer.transfer1(Long.class,
				_$sqlje_rs_gen1);
		return instance_param;
	}

	/** 
 */
	public Long getInstanceCompany(Long instance_id) throws Exception {
		try {
			BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
			Long instance_param = getInstanceParam(instance_id);
			StringBuilder _$sqlje_sql_gen21 = new StringBuilder();
			_$sqlje_sql_gen21.append("\n\t\t\t\tselect ");
			_$sqlje_sql_gen21.append(doc_ref.ref_company_column_name);
			_$sqlje_sql_gen21.append(" \n\t\t\t\tfrom ");
			_$sqlje_sql_gen21.append(doc_ref.document_table_name);
			_$sqlje_sql_gen21.append(" z\n\t\t\t\twhere ");
			_$sqlje_sql_gen21.append(doc_ref.ref_id_column_name);
			_$sqlje_sql_gen21.append(" = ?\n\t\t\t");
			String _$sqlje_sql_gen22 = _$sqlje_sql_gen21.toString();
			PreparedStatement _$sqlje_ps_gen20 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen22);
			_$sqlje_ps_gen20.setLong(1, instance_param);
			$sql.clear();
			_$sqlje_ps_gen20.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen20.getUpdateCount();
			ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen20.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen2);
			getSqlCallStack().push(_$sqlje_ps_gen20);
			Long comapny_id = DataTransfer.transfer1(Long.class,
					_$sqlje_rs_gen2);
			return comapny_id;
		} catch (NoDataFoundException e) {
			return null;
		}
	}

	/**
	 * 取申请者 岗位(position_id)
	 */
	public List<Long> getDocumentPosition(Long instance_id) throws Exception {
		BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
		Long instance_param = getInstanceParam(instance_id);
		StringBuilder _$sqlje_sql_gen24 = new StringBuilder();
		_$sqlje_sql_gen24
				.append("\n\t\t\tselect distinct position_id \n\t\t\tfrom ");
		_$sqlje_sql_gen24.append("(" + doc_ref.ref_detail + ")");
		_$sqlje_sql_gen24.append(" z\n\t\t\twhere ");
		_$sqlje_sql_gen24.append(doc_ref.ref_id_column_name);
		_$sqlje_sql_gen24.append(" = ?\n\t\t");
		String _$sqlje_sql_gen25 = _$sqlje_sql_gen24.toString();
		PreparedStatement _$sqlje_ps_gen23 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen25);
		_$sqlje_ps_gen23.setLong(1, instance_param);
		$sql.clear();
		_$sqlje_ps_gen23.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen23.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen23.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen23);
		List<Long> list = DataTransfer.transferAll(List.class, Long.class,
				_$sqlje_rs_gen3);
		return list;
	}

	/**
	 * 取 申请者(employee_id)
	 */
	public List<Long> getDocumentEmployee(Long instance_id) throws Exception {
		BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
		Long instance_param = getInstanceParam(instance_id);
		StringBuilder _$sqlje_sql_gen27 = new StringBuilder();
		_$sqlje_sql_gen27
				.append("\n\t\t\tselect distinct employee_id \n\t\t\tfrom ");
		_$sqlje_sql_gen27.append("(" + doc_ref.ref_detail + ")");
		_$sqlje_sql_gen27.append(" z\n\t\t\twhere ");
		_$sqlje_sql_gen27.append(doc_ref.ref_id_column_name);
		_$sqlje_sql_gen27.append(" = ?\n\t\t");
		String _$sqlje_sql_gen28 = _$sqlje_sql_gen27.toString();
		PreparedStatement _$sqlje_ps_gen26 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen28);
		_$sqlje_ps_gen26.setLong(1, instance_param);
		$sql.clear();
		_$sqlje_ps_gen26.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen26.getUpdateCount();
		ResultSet _$sqlje_rs_gen4 = _$sqlje_ps_gen26.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen4);
		getSqlCallStack().push(_$sqlje_ps_gen26);
		List<Long> list = DataTransfer.transferAll(List.class, Long.class,
				_$sqlje_rs_gen4);
		return list;
	}

	/**
	 * 取得申请者部门(unit_id)
	 */
	public List<Long> getDocumentUnit(Long instance_id) throws Exception {
		BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
		Long instance_param = getInstanceParam(instance_id);
		StringBuilder _$sqlje_sql_gen30 = new StringBuilder();
		_$sqlje_sql_gen30
				.append("\n\t\t\tselect distinct unit_id \n\t\t\tfrom ");
		_$sqlje_sql_gen30.append("(" + doc_ref.ref_detail + ")");
		_$sqlje_sql_gen30.append(" z\n\t\t\twhere ");
		_$sqlje_sql_gen30.append(doc_ref.ref_id_column_name);
		_$sqlje_sql_gen30.append(" = ?\n\t\t");
		String _$sqlje_sql_gen31 = _$sqlje_sql_gen30.toString();
		PreparedStatement _$sqlje_ps_gen29 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen31);
		_$sqlje_ps_gen29.setLong(1, instance_param);
		$sql.clear();
		_$sqlje_ps_gen29.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen29.getUpdateCount();
		ResultSet _$sqlje_rs_gen5 = _$sqlje_ps_gen29.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen5);
		getSqlCallStack().push(_$sqlje_ps_gen29);
		List<Long> list = DataTransfer.transferAll(List.class, Long.class,
				_$sqlje_rs_gen5);
		return list;
	}

	public Long insertBpmInstanceNodeHirc(Long instance_id, Long usertask_id,
			Long seq_number, Long approver_id, String posted_flag,
			String disabled_flag, String note, Long rule_record_id,
			Long rule_detail_id, Long user_id, String added_order)
			throws Exception {
		BpmnInstanceNodeHierarchy hir = new BpmnInstanceNodeHierarchy();
		hir.instance_id = instance_id;
		hir.usertask_id = usertask_id;
		hir.seq_number = seq_number;
		hir.approver_id = approver_id;
		hir.posted_flag = posted_flag;
		hir.disabled_flag = disabled_flag;
		hir.note = note;
		hir.rule_record_id = rule_record_id;
		hir.rule_detail_id = rule_detail_id;
		hir.added_order = added_order;
		$sql.insert(hir);
		return hir.hierarchy_record_id;
	}

	public Long insertBpmInstanceNodeHirc(Long seq_number, Long approver_id,
			String note, Long rule_record_id, Long rule_detail_id,
			Long user_id, String added_order) throws Exception {
		String _$sqlje_sql_gen33 = "select * from bpmn_instance_node_rule where rule_record_id = ?";
		PreparedStatement _$sqlje_ps_gen32 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen33);
		_$sqlje_ps_gen32.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen32.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen32.getUpdateCount();
		ResultSet _$sqlje_rs_gen6 = _$sqlje_ps_gen32.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen6);
		getSqlCallStack().push(_$sqlje_ps_gen32);
		BpmnInstanceNodeRule rule_record = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen6);
		return insertBpmInstanceNodeHirc(rule_record.instance_id,
				rule_record.usertask_id, seq_number, approver_id, "N", "N",
				note, rule_record_id, rule_detail_id, user_id, added_order);
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public void getEmployeeGrandmanager(String param1, String param2,
			String param3, String param4, Long rule_record_id) throws Exception {
		String _$sqlje_sql_gen35 = "select * from bpmn_instance_node_rule where rule_record_id=?";
		PreparedStatement _$sqlje_ps_gen34 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen35);
		_$sqlje_ps_gen34.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen34.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen34.getUpdateCount();
		ResultSet _$sqlje_rs_gen7 = _$sqlje_ps_gen34.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen7);
		getSqlCallStack().push(_$sqlje_ps_gen34);
		BpmnInstanceNodeRule node_rule = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen7);
		Long company_id = getInstanceCompany(node_rule.instance_id);
		List<Long> positions = getDocumentPosition(node_rule.instance_id);
		if (positions.size() == 0)
			return;
		for (Long position_id : positions) {
			String _$sqlje_sql_gen37 = "select parent_position_id\n\t\t\t\tfrom wfl_position_v\n\t\t\t\twhere position_id = ?";
			PreparedStatement _$sqlje_ps_gen36 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen37);
			_$sqlje_ps_gen36.setLong(1, position_id);
			$sql.clear();
			_$sqlje_ps_gen36.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen36.getUpdateCount();
			ResultSet _$sqlje_rs_gen8 = _$sqlje_ps_gen36.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen8);
			getSqlCallStack().push(_$sqlje_ps_gen36);
			for (Long parent_position : new ResultSetIterator<Long>(
					_$sqlje_rs_gen8, Long.class)) {
				String _$sqlje_sql_gen39 = "select parent_position_id\n\t\t\t\t\t\tfrom wfl_position_v\n\t\t\t\t\t\twhere position_id = ?";
				PreparedStatement _$sqlje_ps_gen38 = getSqlCallStack()
						.getCurrentConnection().prepareStatement(
								_$sqlje_sql_gen39);
				_$sqlje_ps_gen38.setLong(1, parent_position);
				$sql.clear();
				_$sqlje_ps_gen38.execute();
				$sql.UPDATECOUNT = _$sqlje_ps_gen38.getUpdateCount();
				ResultSet _$sqlje_rs_gen9 = _$sqlje_ps_gen38.getResultSet();
				getSqlCallStack().push(_$sqlje_rs_gen9);
				getSqlCallStack().push(_$sqlje_ps_gen38);
				for (Long grandparent_position : new ResultSetIterator<Long>(
						_$sqlje_rs_gen9, Long.class)) {
					String _$sqlje_sql_gen41 = "SELECT DISTINCT u.user_id\n                            FROM wfl_employee_assigns_v a, wfl_users_v u\n                           WHERE a.position_id = ?\n                             AND a.employee_id = u.employee_id\n                             AND a.company_id = ?";
					PreparedStatement _$sqlje_ps_gen40 = getSqlCallStack()
							.getCurrentConnection().prepareStatement(
									_$sqlje_sql_gen41);
					_$sqlje_ps_gen40.setLong(1, grandparent_position);
					_$sqlje_ps_gen40.setLong(2, company_id);
					$sql.clear();
					_$sqlje_ps_gen40.execute();
					$sql.UPDATECOUNT = _$sqlje_ps_gen40.getUpdateCount();
					ResultSet _$sqlje_rs_gen10 = _$sqlje_ps_gen40
							.getResultSet();
					getSqlCallStack().push(_$sqlje_rs_gen10);
					getSqlCallStack().push(_$sqlje_ps_gen40);
					for (Long approver_id : new ResultSetIterator<Long>(
							_$sqlje_rs_gen10, Long.class)) {
						insertBpmInstanceNodeHirc(node_rule.recipient_sequence,
								approver_id, "", rule_record_id, null, 1L, null);
					}
				}
			}
		}
	}

	public void getEmployee(String param1, String param2, String param3,
			String param4, Long rule_record_id) throws Exception {
		String _$sqlje_sql_gen43 = "select * from bpmn_instance_node_rule where rule_record_id = ?";
		PreparedStatement _$sqlje_ps_gen42 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen43);
		_$sqlje_ps_gen42.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen42.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen42.getUpdateCount();
		ResultSet _$sqlje_rs_gen11 = _$sqlje_ps_gen42.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen11);
		getSqlCallStack().push(_$sqlje_ps_gen42);
		BpmnInstanceNodeRule node_rule = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen11);
		List<Long> emps = getDocumentEmployee(node_rule.instance_id);
		if (emps.size() == 0)
			return;
		for (Long document_employee_id : emps) {
			String _$sqlje_sql_gen45 = "select distinct u.user_id\n\t\t\t\t\t\t\t\t\tfrom wfl_users_v u\n\t\t\t\t\t\t\t\t\twhere u.employee_id=?";
			PreparedStatement _$sqlje_ps_gen44 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen45);
			_$sqlje_ps_gen44.setLong(1, document_employee_id);
			$sql.clear();
			_$sqlje_ps_gen44.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen44.getUpdateCount();
			ResultSet _$sqlje_rs_gen12 = _$sqlje_ps_gen44.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen12);
			getSqlCallStack().push(_$sqlje_ps_gen44);
			for (Long approver_id : new ResultSetIterator<Long>(
					_$sqlje_rs_gen12, Long.class)) {
				insertBpmInstanceNodeHirc(node_rule.recipient_sequence,
						approver_id, "", rule_record_id, null, 1L, null);
			}
		}
	}

	/**
	 * 取得申请者部门主管
	 */
	public void getEmployeeUnitManager(String param1, String param2,
			String param3, String param4, Long rule_record_id) throws Exception {
		String _$sqlje_sql_gen47 = "select * from bpmn_instance_node_rule where rule_record_id=?";
		PreparedStatement _$sqlje_ps_gen46 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen47);
		_$sqlje_ps_gen46.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen46.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen46.getUpdateCount();
		ResultSet _$sqlje_rs_gen13 = _$sqlje_ps_gen46.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen13);
		getSqlCallStack().push(_$sqlje_ps_gen46);
		BpmnInstanceNodeRule node_rule = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen13);
		Long company_id = getInstanceCompany(node_rule.instance_id);
		List<Long> positions = getDocumentPosition(node_rule.instance_id);
		if (positions.size() == 0)
			return;
		for (Long position_id : positions) {
			String _$sqlje_sql_gen49 = "SELECT n.chief_position_id\n\t                FROM wfl_position_v p, wfl_unit_v n\n\t               WHERE p.position_id = ?\n\t                 AND p.unit_id = n.unit_id";
			PreparedStatement _$sqlje_ps_gen48 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen49);
			_$sqlje_ps_gen48.setLong(1, position_id);
			$sql.clear();
			_$sqlje_ps_gen48.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen48.getUpdateCount();
			ResultSet _$sqlje_rs_gen14 = _$sqlje_ps_gen48.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen14);
			getSqlCallStack().push(_$sqlje_ps_gen48);
			for (Long chief_position_id : new ResultSetIterator<Long>(
					_$sqlje_rs_gen14, Long.class)) {
				String _$sqlje_sql_gen51 = "SELECT DISTINCT u.user_id\n\t                    FROM wfl_employee_assigns_v a, wfl_users_v u\n\t                   WHERE a.position_id = ?\n\t                     AND a.employee_id = u.employee_id\n\t                     AND a.company_id = ?";
				PreparedStatement _$sqlje_ps_gen50 = getSqlCallStack()
						.getCurrentConnection().prepareStatement(
								_$sqlje_sql_gen51);
				_$sqlje_ps_gen50.setLong(1, chief_position_id);
				_$sqlje_ps_gen50.setLong(2, company_id);
				$sql.clear();
				_$sqlje_ps_gen50.execute();
				$sql.UPDATECOUNT = _$sqlje_ps_gen50.getUpdateCount();
				ResultSet _$sqlje_rs_gen15 = _$sqlje_ps_gen50.getResultSet();
				getSqlCallStack().push(_$sqlje_rs_gen15);
				getSqlCallStack().push(_$sqlje_ps_gen50);
				for (Long approver_id : new ResultSetIterator<Long>(
						_$sqlje_rs_gen15, Long.class)) {
					insertBpmInstanceNodeHirc(node_rule.recipient_sequence,
							approver_id, "", rule_record_id, null, 1L, null);
				}
			}
		}
	}

	/**
	 * 取得申请者主管
	 */
	public void getEmployeeManager(String param1, String param2, String param3,
			String param4, Long rule_record_id) throws Exception {
	}

	/**
	 * 取得申请者间接主管
	 */
	public void getEmployeeIndirectManager(String param1, String param2,
			String param3, String param4, Long rule_record_id) throws Exception {
	}

	public void getEmployeeCompanyManager(String param1, String param2,
			String param3, String param4, Long rule_record_id) throws Exception {
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
