package aurora.bpm.command.sqlje;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_INSTANCE_NODE_RECIPIENT",stdwho=false)
public class BpmnInstanceNodeRecipient
{
	/**审批记录ID*/
	@PK
	public Long record_id;
	/**工作流实例ID*/
	public Long instance_id;
	/**工作流节点ID*/
	public Long usertask_id;
	/**工作流节点序号*/
	public Long seq_number;
	/**用户ID*/
	public Long user_id;
	/**审批时限*/
	public java.sql.Timestamp date_limit;
	/**转交人ID*/
	public Long commision_by;
	/**转交人*/
	public String commision_desc;
	/**最后通知时间*/
	public java.sql.Timestamp last_notify_date;
	/**附件ID*/
	public Long attachment_id;
	/**审批层次记录ID*/
	public Long hierarchy_record_id;
	/**创建日期*/
	public java.sql.Timestamp creation_date;
	/**创建用户ID*/
	public Long created_by;
	/**最后更新日期*/
	public java.sql.Timestamp last_update_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
}
