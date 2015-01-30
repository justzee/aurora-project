package aurora.bpm.command.sqlje;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_APPROVE_RECORD",stdwho=false)
public class BpmnApproveRecord
{
	/**审批记录ID*/
	@PK
	public Long record_id;
	/**工作流实例ID*/
	public Long instance_id;
	/**工作流节点ID*/
	public Long usertask_id;
	/**审批动作ID*/
	public String action_token;
	/**审批备注*/
	public String comment_text;
	/**审批轮次*/
	public Long approve_count;
	/**附件ID*/
	public Long attachment_id;
	/**工作流节点序号*/
	public Long seq_number;
	/**待办记录ID*/
	public Long rcpt_record_id;
	/**失效标志*/
	public String disabled_flag;
	/**记录备注*/
	public String note;
	/**创建用户ID*/
	public Long created_by;
	/**创建日期*/
	public java.sql.Timestamp creation_date;
	/**最后更新日期*/
	public java.sql.Timestamp last_update_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
}
