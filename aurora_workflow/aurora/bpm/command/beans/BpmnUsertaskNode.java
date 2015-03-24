package aurora.bpm.command.beans;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_USERTASK_NODE",stdwho=true)
public class BpmnUsertaskNode
{
	/**pk*/
	@PK
	public Long usertask_id;
	/**process code*/
	public String process_code;
	/**工作流版本*/
	public String process_version;
	/**user task 节点ID*/
	public String node_id;
	/**接收类型*/
	public Long recipient_type;
	/**是否邮件提醒*/
	public Integer mail_notify;
	/**处理日期限制*/
	public Integer is_date_limited;
	/**处理日期*/
	public Long process_date;
	/**时间单位*/
	public String process_date_unit_id;
	/**有效日期从*/
	public java.sql.Date date_from;
	/**有效日期到*/
	public java.sql.Date date_to;
	public Long object_version_number;
	/**审批类型*/
	public Long approval_type;
	public String recipient_proc;
	public Long name_id;
	public Long description_id;
	/**是否可以转交*/
	public Long can_deliver_to;
	/**邮件模板*/
	public Long mail_template;
	/**提醒周期*/
	public Long notify_period;
	/**时间单位*/
	public String notify_period_length;
	/**结束时是否通知处理者*/
	public Long notify_on_finish;
	/**校验的存储过程*/
	public String check_proc;
	/**无需重复审批*/
	public Long can_auto_pass;
	/**节点前处理过程*/
	public String pre_node_proc;
	/**审批意见查看限制*/
	public Long is_comment_access_control;
	/**数值*/
	public Long quantity;
	/**处理中页面*/
	public Long processing_page;
	/**已处理页面*/
	public Long processed_page;
	/**通知页面*/
	public Long notify_page;
	/**提交人是否需要审批*/
	public Long is_self_re_commit;
	/**节点允许无审批人*/
	public Long can_no_approver;
	/**允许添加审批人*/
	public Long can_add_approver;
	/**允许增加通知人*/
	public Long can_add_notification;
	/**创建用户ID*/
	public Long created_by;
	/**创建日期*/
	public java.sql.Date creation_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
	/**最后更新日期*/
	public java.sql.Date last_update_date;
}
