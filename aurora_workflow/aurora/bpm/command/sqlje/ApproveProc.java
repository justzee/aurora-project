package aurora.bpm.command.sqlje;

import java.util.Arrays;
import uncertain.composite.*;
import aurora.bpm.command.beans.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class ApproveProc implements aurora.sqlje.core.ISqlCallEnabled {
	public BpmnUsertaskNode queryByRecipientRecordId(Long record_id)
			throws Exception {
		String _$sqlje_sql_gen10 = "\n\t\t\tselect * from bpmn_usertask_node\n\t\t\twhere usertask_id = (\n\t\t\t\t\tselect usertask_id from bpmn_instance_node_recipient\n\t\t\t\t\twhere record_id = ?)\n\t\t";
		PreparedStatement _$sqlje_ps_gen9 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen10);
		_$sqlje_ps_gen9.setLong(1, record_id);
		$sql.clear();
		_$sqlje_ps_gen9.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen9.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen9.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen9);
		BpmnUsertaskNode bun = DataTransfer.transfer1(BpmnUsertaskNode.class,
				_$sqlje_rs_gen0);
		return bun;
	}

	public String approve(Long instance_id, Long rcpt_record_id, Long user_id,
			String action_code, String approve_content) throws Exception {
		BpmnInstanceNodeRecipient rcpt = null;
		try {
			String _$sqlje_sql_gen12 = "select * from bpmn_instance_node_recipient where record_id = ?";
			PreparedStatement _$sqlje_ps_gen11 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen12);
			_$sqlje_ps_gen11.setLong(1, rcpt_record_id);
			$sql.clear();
			_$sqlje_ps_gen11.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen11.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen11.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen11);
			rcpt = DataTransfer.transfer1(BpmnInstanceNodeRecipient.class,
					_$sqlje_rs_gen1);
		} catch (NoDataFoundException e) {
			throw new Exception("工作流审批 : 未找到代办记录:record_id:" + rcpt_record_id);
		}
		if (checkApproveValidation(rcpt_record_id, action_code, user_id) == 0) {
			throw new Exception("工作流审批 ：审批权限交验结果为'否', 工作流审批中止.");
		}
		if (!eq(rcpt.user_id, user_id)) {
			System.out.println("rcpt.user_id=" + rcpt.user_id + "  user_id="
					+ user_id);
			throw new Exception("工作流审批 ：user_id 权限检查结果为'否',工作流审批中止");
		}
		Long usertask_id = rcpt.usertask_id;
		String _$sqlje_sql_gen14 = "select count(1) \n\t\t\tfrom bpmn_usertask_node_action \n\t\t\twhere usertask_id=? \n\t\t\tand coalesce(action_code_custom,action_code)=? ";
		PreparedStatement _$sqlje_ps_gen13 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen14);
		_$sqlje_ps_gen13.setLong(1, usertask_id);
		_$sqlje_ps_gen13.setString(2, action_code);
		$sql.clear();
		_$sqlje_ps_gen13.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen13.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen13.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen13);
		Long exists = DataTransfer.transfer1(Long.class, _$sqlje_rs_gen2);
		if (exists == 0) {
			throw new Exception("工作流审批 : 不能进行指定的操作 " + action_code);
		}
		String _$sqlje_sql_gen16 = "select n.attachment_id from bpmn_instance_node_recipient n where n.record_id=?";
		PreparedStatement _$sqlje_ps_gen15 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen16);
		_$sqlje_ps_gen15.setLong(1, rcpt_record_id);
		$sql.clear();
		_$sqlje_ps_gen15.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen15.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen15.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen15);
		Long attachment_id = DataTransfer
				.transfer1(Long.class, _$sqlje_rs_gen3);
		Long is_required = 0L;
		Long approve_record_id = createApproveRecord(instance_id, usertask_id,
				rcpt.seq_number, action_code, approve_content, rcpt_record_id,
				attachment_id, user_id);
		String _$sqlje_sql_gen18 = "delete from bpmn_instance_node_recipient where record_id=?";
		PreparedStatement _$sqlje_ps_gen17 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen18);
		_$sqlje_ps_gen17.setLong(1, rcpt_record_id);
		$sql.clear();
		_$sqlje_ps_gen17.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen17.getUpdateCount();
		ResultSet _$sqlje_rs_gen4 = _$sqlje_ps_gen17.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen4);
		getSqlCallStack().push(_$sqlje_ps_gen17);
		if (!in(action_code, "AGREE", "REJECT")) {
			System.out.println("工作流审批 : 动作为自定义" + action_code + ",直接返回.");
			return action_code;
		}
		String _$sqlje_sql_gen20 = "select * from bpmn_usertask_node where usertask_id=?";
		PreparedStatement _$sqlje_ps_gen19 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen20);
		_$sqlje_ps_gen19.setLong(1, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen19.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen19.getUpdateCount();
		ResultSet _$sqlje_rs_gen5 = _$sqlje_ps_gen19.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen5);
		getSqlCallStack().push(_$sqlje_ps_gen19);
		BpmnUsertaskNode bun = DataTransfer.transfer1(BpmnUsertaskNode.class,
				_$sqlje_rs_gen5);
		if (in(bun.approval_type, 0L, 1L, 2L, 4L, 7L)) {
			return approveByApproverCount(instance_id, usertask_id, bun);
		}
		return "";
	}

	public Long createApproveRecord(Long instance_id, Long usertask_id,
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
	private String approveByApproverCount(Long instance_id, Long usertask_id,
			BpmnUsertaskNode bun) throws Exception {
		String _$sqlje_sql_gen22 = "\n\t\t\t\tselect count(1) from bpmn_instance_node_hierarchy\n\t\t\t\t\twhere instance_id=?\n\t\t\t\t\tand usertask_id=?\n\t\t\t\t\tand coalesce(disabled_flag,'N')='N' ";
		PreparedStatement _$sqlje_ps_gen21 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen22);
		_$sqlje_ps_gen21.setLong(1, instance_id);
		_$sqlje_ps_gen21.setLong(2, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen21.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen21.getUpdateCount();
		ResultSet _$sqlje_rs_gen6 = _$sqlje_ps_gen21.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen6);
		getSqlCallStack().push(_$sqlje_ps_gen21);
		Long all_approver_count = DataTransfer.transfer1(Long.class,
				_$sqlje_rs_gen6);
		String _$sqlje_sql_gen24 = "\n\t\t\t\tselect count(1) from bpmn_approve_record r\n\t\t\t\t\twhere r.instance_id=?\n\t\t\t\t\tand r.usertask_id=?\n\t\t\t\t\tand r.action_token='REJECT'\n\t\t\t\t\tand coalesce(disabled_flag,'N')='N'";
		PreparedStatement _$sqlje_ps_gen23 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen24);
		_$sqlje_ps_gen23.setLong(1, instance_id);
		_$sqlje_ps_gen23.setLong(2, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen23.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen23.getUpdateCount();
		ResultSet _$sqlje_rs_gen7 = _$sqlje_ps_gen23.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen7);
		getSqlCallStack().push(_$sqlje_ps_gen23);
		Long terminate_count = DataTransfer.transfer1(Long.class,
				_$sqlje_rs_gen7);
		String _$sqlje_sql_gen26 = "\n\t\t\t\tselect count(1) from bpmn_approve_record r\n\t\t\t\t\twhere r.instance_id=?\n\t\t\t\t\tand r.usertask_id = ?\n\t\t\t\t\tand r.action_token= 'AGREE'\n\t\t\t\t\tand coalesce(disabled_flag,'N')='N'";
		PreparedStatement _$sqlje_ps_gen25 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen26);
		_$sqlje_ps_gen25.setLong(1, instance_id);
		_$sqlje_ps_gen25.setLong(2, usertask_id);
		$sql.clear();
		_$sqlje_ps_gen25.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen25.getUpdateCount();
		ResultSet _$sqlje_rs_gen8 = _$sqlje_ps_gen25.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen8);
		getSqlCallStack().push(_$sqlje_ps_gen25);
		Long passed_count = DataTransfer.transfer1(Long.class, _$sqlje_rs_gen8);
		Long quantity = nvl(bun.quantity, 0L);
		if (bun.approval_type == 0) {
			if (terminate_count == all_approver_count)
				return "REJECT";
		} else if (bun.approval_type == 1) {
			if (terminate_count > 0)
				return "REJECT";
		} else if (bun.approval_type == 2) {
			if (all_approver_count == 0)
				return "REJECT";
			if (terminate_count * 100d / all_approver_count > (100 - quantity))
				return "REJECT";
			if (passed_count * 100d / all_approver_count > quantity)
				return "AGREE";
		} else if (bun.approval_type == 4) {
			if (terminate_count > (all_approver_count - quantity))
				return "REJECT";
			if (passed_count > quantity)
				return "AGREE";
		} else if (bun.approval_type == 6) {
			if (terminate_count > 0 && passed_count > 0)
				System.err.println("一票通过/拒绝 数据异常");
			if (terminate_count > 0)
				return "REJECT";
			if (passed_count > 0)
				return "AGREE";
		}
		if (terminate_count == all_approver_count)
			return "REJECT";
		else if (passed_count == all_approver_count)
			return "AGREE";
		return "";
	}

	private Long approveByRuleCount() throws Exception {
		return 1L;
	}

	public Long checkApproveValidation(Long rcpt_record_id, String action_code,
			Long user_id) throws Exception {
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
