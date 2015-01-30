package aurora.bpm.command.sqlje;

import java.util.Arrays;
import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class approve implements aurora.sqlje.core.ISqlCallEnabled {
	public BpmnUsertaskNode queryByRecipientRecordId(Long record_id)
			throws Exception {
		String _$sqlje_sql_gen9 = "\n\t\t\tselect * from bpmn_usertask_node\n\t\t\twhere usertask_id = (\n\t\t\t\t\tselect usertask_id from bpmn_instance_node_recipient\n\t\t\t\t\twhere record_id = ?)\n\t\t";
		PreparedStatement _$sqlje_ps_gen8 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen9);
		_$sqlje_ps_gen8.setLong(1, record_id);
		$sql.clear();
		_$sqlje_ps_gen8.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen8.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen8.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen8);
		BpmnUsertaskNode bun = DataTransfer.transfer1(BpmnUsertaskNode.class,
				_$sqlje_rs_gen0);
		return bun;
	}

	public Long approve(Long instance_id, Long rcpt_record_id, Long user_id,
			String action_code, String approve_content) throws Exception {
		BpmnInstanceNodeRecipient rcpt = null;
		try {
			String _$sqlje_sql_gen11 = "select * from bpmn_instance_node_recipient where record_id = ?";
			PreparedStatement _$sqlje_ps_gen10 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen11);
			_$sqlje_ps_gen10.setLong(1, rcpt_record_id);
			$sql.clear();
			_$sqlje_ps_gen10.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen10.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen10.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen10);
			rcpt = DataTransfer.transfer1(BpmnInstanceNodeRecipient.class,
					_$sqlje_rs_gen1);
		} catch (NoDataFoundException e) {
			throw new Exception("工作流审批 : 未找到代办记录:record_id:" + rcpt_record_id);
		}
		if (check_approve_validation(rcpt_record_id, action_code, user_id) == 0) {
			throw new Exception("工作流审批 ：审批权限交验结果为'否', 工作流审批中止.");
		}
		if (!eq(rcpt.user_id, user_id)) {
			System.out.println("工作流审批 ：user_id 权限检查结果为'否',工作流审批中止");
			return -2L;
		}
		Long usertask_id = rcpt.usertask_id;
		String _$sqlje_sql_gen13 = "select n.attachment_id from bpmn_instance_node_recipient n where n.record_id=?";
		PreparedStatement _$sqlje_ps_gen12 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen13);
		_$sqlje_ps_gen12.setLong(1, rcpt_record_id);
		$sql.clear();
		_$sqlje_ps_gen12.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen12.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen12.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen12);
		Long attachment_id = DataTransfer
				.transfer1(Long.class, _$sqlje_rs_gen2);
		Long is_required = 0L;
		Long approve_record_id = create_approve_record(instance_id,
				usertask_id, rcpt.seq_number, action_code, approve_content,
				rcpt_record_id, attachment_id, user_id);
		String _$sqlje_sql_gen15 = "delete from bpmn_instance_node_recipient where record_id=?";
		PreparedStatement _$sqlje_ps_gen14 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen15);
		_$sqlje_ps_gen14.setLong(1, rcpt_record_id);
		$sql.clear();
		_$sqlje_ps_gen14.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen14.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen14.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen14);
		String _$sqlje_sql_gen17 = "select * from bpmn_usertask_node where usertask_id=?";
		PreparedStatement _$sqlje_ps_gen16 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen17);
		_$sqlje_ps_gen16.setLong(1, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen16.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen16.getUpdateCount();
		ResultSet _$sqlje_rs_gen4 = _$sqlje_ps_gen16.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen4);
		getSqlCallStack().push(_$sqlje_ps_gen16);
		BpmnUsertaskNode bun = DataTransfer.transfer1(BpmnUsertaskNode.class,
				_$sqlje_rs_gen4);
		if (in(bun.approval_type, 0L, 1L, 2L, 4L, 7L)) {
			return approveByApproverCount(instance_id, usertask_id, bun);
		}
		return 0L;
	}

	public Long create_approve_record(Long instance_id, Long usertask_id,
			Long seq_number, String action_code, String approve_content,
			Long rcpt_record_id, Long attachment_id, Long user_id)
			throws Exception {
		BpmnApproveRecord bar = new BpmnApproveRecord();
		bar.instance_id = instance_id;
		bar.usertask_id = usertask_id;
		bar.seq_number = seq_number;
		bar.action_token = action_code;
		bar.comment_text = approve_content;
		bar.rcpt_record_id = rcpt_record_id;
		bar.attachment_id = attachment_id;
		$sql.insert(bar);
		return bar.record_id;
	}

	/**
	 * 按人数审批<br>
	 * 
	 * @return 返回 -1,审批拒绝<br>
	 *         返回 1,审批通过<br>
	 *         返回 0,审批尚未得出结果,需等待其他审批人
	 */
	private Long approveByApproverCount(Long instance_id, Long usertask_id,
			BpmnUsertaskNode bun) throws Exception {
		String _$sqlje_sql_gen19 = "\n\t\t\t\tselect count(1) from bpmn_instance_node_hierarchy\n\t\t\t\t\twhere instance_id=?\n\t\t\t\t\tand usertask_id=?\n\t\t\t\t\tand coalesce(disabled_flag,'N')='N' ";
		PreparedStatement _$sqlje_ps_gen18 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen19);
		_$sqlje_ps_gen18.setLong(1, instance_id);
		_$sqlje_ps_gen18.setLong(2, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen18.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen18.getUpdateCount();
		ResultSet _$sqlje_rs_gen5 = _$sqlje_ps_gen18.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen5);
		getSqlCallStack().push(_$sqlje_ps_gen18);
		Long all_approver_count = DataTransfer.transfer1(Long.class,
				_$sqlje_rs_gen5);
		String _$sqlje_sql_gen21 = "\n\t\t\t\tselect count(1) from bpmn_approve_record r\n\t\t\t\t\twhere r.instance_id=?\n\t\t\t\t\tand r.usertask_id=?\n\t\t\t\t\tand r.action_token='REJECT'\n\t\t\t\t\tand coalesce(disabled_flag,'N')='N'";
		PreparedStatement _$sqlje_ps_gen20 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen21);
		_$sqlje_ps_gen20.setLong(1, instance_id);
		_$sqlje_ps_gen20.setLong(2, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen20.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen20.getUpdateCount();
		ResultSet _$sqlje_rs_gen6 = _$sqlje_ps_gen20.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen6);
		getSqlCallStack().push(_$sqlje_ps_gen20);
		Long terminate_count = DataTransfer.transfer1(Long.class,
				_$sqlje_rs_gen6);
		String _$sqlje_sql_gen23 = "\n\t\t\t\tselect count(1) from bpmn_approve_record r\n\t\t\t\t\twhere r.instance_id=?\n\t\t\t\t\tand r.usertask_id = ?\n\t\t\t\t\tand r.action_token= 'PASS'\n\t\t\t\t\tand coalesce(disabled_flag,'N')='N'";
		PreparedStatement _$sqlje_ps_gen22 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen23);
		_$sqlje_ps_gen22.setLong(1, instance_id);
		_$sqlje_ps_gen22.setLong(2, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen22.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen22.getUpdateCount();
		ResultSet _$sqlje_rs_gen7 = _$sqlje_ps_gen22.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen7);
		getSqlCallStack().push(_$sqlje_ps_gen22);
		Long passed_count = DataTransfer.transfer1(Long.class, _$sqlje_rs_gen7);
		Long quantity = nvl(bun.quantity, 0L);
		if (bun.approval_type == 0) {
			if (terminate_count == all_approver_count)
				return -1L;
		} else if (bun.approval_type == 1) {
			if (terminate_count > 0)
				return -1L;
		} else if (bun.approval_type == 2) {
			if (all_approver_count == 0)
				return -1L;
			if (terminate_count * 100d / all_approver_count > (100 - quantity))
				return -1L;
			if (passed_count * 100d / all_approver_count > quantity)
				return 1L;
		} else if (bun.approval_type == 4) {
			if (terminate_count > (all_approver_count - quantity))
				return -1L;
			if (passed_count > quantity)
				return 1L;
		} else if (bun.approval_type == 6) {
			if (terminate_count > 0 && passed_count > 0)
				System.err.println("一票通过/拒绝 数据异常");
			if (terminate_count > 0)
				return -1L;
			if (passed_count > 0)
				return 1L;
		}
		if (terminate_count == all_approver_count)
			return -1L;
		else if (passed_count == all_approver_count)
			return 1L;
		return 0L;
	}

	private Long approveByRuleCount() throws Exception {
		return 1L;
	}

	public Long check_approve_validation(Long rcpt_record_id,
			String action_code, Long user_id) throws Exception {
		return 1L;
	}

	public static boolean eq(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public static boolean in(Object o0, Object... args) {
		return Arrays.asList(args).contains(o0);
	}

	public static <T> T coalesce(T obj, T def) {
		if (obj == null)
			return def;
		return obj;
	}

	public static <T> T nvl(T obj, T def) {
		return coalesce(obj, def);
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
