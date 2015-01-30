package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.util.List;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class recipient_rule_common implements aurora.sqlje.core.ISqlCallEnabled {
	public BpmnDocumentReference getDocumentReference(Long instance_id)
			throws Exception {
		String _$sqlje_sql_gen8 = "select *\n\t\t\t  from bpmn_document_reference\n\t\t\t  where category_id = (select d.category_id\n\t\t\t                         from bpmn_process_define d, bpmn_process_instance i\n\t\t\t                        where d.process_code = i.process_code\n\t\t\t                          and d.process_version = i.process_version\n\t\t\t                          and i.instance_id = ?)\n\t\t";
		PreparedStatement _$sqlje_ps_gen7 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen8);
		_$sqlje_ps_gen7.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen7.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen7.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen7.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen7);
		BpmnDocumentReference doc_ref = DataTransfer.transfer1(
				BpmnDocumentReference.class, _$sqlje_rs_gen0);
		return doc_ref;
	}

	public Long getInstanceParam(Long instance_id) throws Exception {
		String _$sqlje_sql_gen10 = "select instance_param from bpmn_process_instance where instance_id=?";
		PreparedStatement _$sqlje_ps_gen9 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen10);
		_$sqlje_ps_gen9.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen9.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen9.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen9.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen9);
		Long instance_param = DataTransfer.transfer1(Long.class,
				_$sqlje_rs_gen1);
		return instance_param;
	}

	public Long get_instance_company(Long instance_id) throws Exception {
		try {
			BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
			Long instance_param = getInstanceParam(instance_id);
			StringBuilder _$sqlje_sql_gen12 = new StringBuilder();
			_$sqlje_sql_gen12.append("\n\t\t\t\tselect ");
			_$sqlje_sql_gen12.append(doc_ref.ref_company_column_name);
			_$sqlje_sql_gen12.append(" \n\t\t\t\tfrom ");
			_$sqlje_sql_gen12.append(doc_ref.document_table_name);
			_$sqlje_sql_gen12.append(" z\n\t\t\t\twhere ");
			_$sqlje_sql_gen12.append(doc_ref.ref_id_column_name);
			_$sqlje_sql_gen12.append(" = ?\n\t\t\t");
			String _$sqlje_sql_gen13 = _$sqlje_sql_gen12.toString();
			PreparedStatement _$sqlje_ps_gen11 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen13);
			_$sqlje_ps_gen11.setLong(1, instance_param);
			$sql.clear();
			_$sqlje_ps_gen11.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen11.getUpdateCount();
			ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen11.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen2);
			getSqlCallStack().push(_$sqlje_ps_gen11);
			Long comapny_id = DataTransfer.transfer1(Long.class,
					_$sqlje_rs_gen2);
			return comapny_id;
		} catch (NoDataFoundException e) {
			return null;
		}
	}

	/**
	 * 取申请者 岗位
	 */
	public List<Long> get_document_position(Long instance_id) throws Exception {
		BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
		Long instance_param = getInstanceParam(instance_id);
		StringBuilder _$sqlje_sql_gen15 = new StringBuilder();
		_$sqlje_sql_gen15
				.append("\n\t\t\tselect distinct position_id \n\t\t\tfrom ");
		_$sqlje_sql_gen15.append("(" + doc_ref.ref_detail + ")");
		_$sqlje_sql_gen15.append(" z\n\t\t\twhere ");
		_$sqlje_sql_gen15.append(doc_ref.ref_id_column_name);
		_$sqlje_sql_gen15.append(" = ?\n\t\t");
		String _$sqlje_sql_gen16 = _$sqlje_sql_gen15.toString();
		PreparedStatement _$sqlje_ps_gen14 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen16);
		_$sqlje_ps_gen14.setLong(1, instance_param);
		$sql.clear();
		_$sqlje_ps_gen14.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen14.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen14.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen14);
		List<Long> list = DataTransfer.transferAll(List.class, Long.class,
				_$sqlje_rs_gen3);
		return list;
	}

	/**
	 * 取 申请者
	 */
	public List<Long> get_document_employee(Long instance_id) throws Exception {
		BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
		Long instance_param = getInstanceParam(instance_id);
		StringBuilder _$sqlje_sql_gen18 = new StringBuilder();
		_$sqlje_sql_gen18
				.append("\n\t\t\tselect distinct employee_id \n\t\t\tfrom ");
		_$sqlje_sql_gen18.append("(" + doc_ref.ref_detail + ")");
		_$sqlje_sql_gen18.append(" z\n\t\t\twhere ");
		_$sqlje_sql_gen18.append(doc_ref.ref_id_column_name);
		_$sqlje_sql_gen18.append(" = ?\n\t\t");
		String _$sqlje_sql_gen19 = _$sqlje_sql_gen18.toString();
		PreparedStatement _$sqlje_ps_gen17 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen19);
		_$sqlje_ps_gen17.setLong(1, instance_param);
		$sql.clear();
		_$sqlje_ps_gen17.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen17.getUpdateCount();
		ResultSet _$sqlje_rs_gen4 = _$sqlje_ps_gen17.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen4);
		getSqlCallStack().push(_$sqlje_ps_gen17);
		List<Long> list = DataTransfer.transferAll(List.class, Long.class,
				_$sqlje_rs_gen4);
		return list;
	}

	/**
	 * 取得申请者部门
	 */
	public List<Long> get_document_unit(Long instance_id) throws Exception {
		BpmnDocumentReference doc_ref = getDocumentReference(instance_id);
		Long instance_param = getInstanceParam(instance_id);
		StringBuilder _$sqlje_sql_gen21 = new StringBuilder();
		_$sqlje_sql_gen21
				.append("\n\t\t\tselect distinct unit_id \n\t\t\tfrom ");
		_$sqlje_sql_gen21.append("(" + doc_ref.ref_detail + ")");
		_$sqlje_sql_gen21.append(" z\n\t\t\twhere ");
		_$sqlje_sql_gen21.append(doc_ref.ref_id_column_name);
		_$sqlje_sql_gen21.append(" = ?\n\t\t");
		String _$sqlje_sql_gen22 = _$sqlje_sql_gen21.toString();
		PreparedStatement _$sqlje_ps_gen20 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen22);
		_$sqlje_ps_gen20.setLong(1, instance_param);
		$sql.clear();
		_$sqlje_ps_gen20.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen20.getUpdateCount();
		ResultSet _$sqlje_rs_gen5 = _$sqlje_ps_gen20.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen5);
		getSqlCallStack().push(_$sqlje_ps_gen20);
		List<Long> list = DataTransfer.transferAll(List.class, Long.class,
				_$sqlje_rs_gen5);
		return list;
	}

	public Long insert_wfl_instance_node_hirc(Long instance_id,
			Long usertask_id, Long seq_number, Long approver_id,
			String posted_flag, String disabled_flag, String note,
			Long rule_record_id, Long rule_detail_id, Long user_id,
			String added_order) throws Exception {
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

	public Long insert_wfl_instance_node_hirc(Long seq_number,
			Long approver_id, String note, Long rule_record_id,
			Long rule_detail_id, Long user_id, String added_order)
			throws Exception {
		String _$sqlje_sql_gen24 = "select * from bpmn_instance_node_rule where rule_record_id = ?";
		PreparedStatement _$sqlje_ps_gen23 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen24);
		_$sqlje_ps_gen23.setLong(1, rule_record_id);
		$sql.clear();
		_$sqlje_ps_gen23.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen23.getUpdateCount();
		ResultSet _$sqlje_rs_gen6 = _$sqlje_ps_gen23.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen6);
		getSqlCallStack().push(_$sqlje_ps_gen23);
		BpmnInstanceNodeRule rule_record = DataTransfer.transfer1(
				BpmnInstanceNodeRule.class, _$sqlje_rs_gen6);
		return insert_wfl_instance_node_hirc(rule_record.instance_id,
				rule_record.usertask_id, seq_number, approver_id, "N", "N",
				note, rule_record_id, rule_detail_id, user_id, added_order);
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
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
