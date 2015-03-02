package aurora.bpm.command.beans;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_INSTANCE_NODE_HIERARCHY",stdwho=false)
public class BpmnInstanceNodeHierarchy
{
	/**审批层次记录ID*/
	@PK
	public Long hierarchy_record_id;
	/**工作流实例ID*/
	public Long instance_id;
	/**工作流节点ID*/
	public Long usertask_id;
	/**审批顺序号*/
	public Long seq_number;
	/**审批者ID*/
	public Long approver_id;
	/**是否生成待办记录标志*/
	public String posted_flag;
	/**失效标志*/
	public String disabled_flag;
	/**说明*/
	public String note;
	/**审批规则ID*/
	public Long rule_record_id;
	/**审批规则明细ID*/
	public Long rule_detail_id;
	/**创建日期*/
	public java.sql.Timestamp creation_date;
	/**创建用户ID*/
	public Long created_by;
	/**最后更新日期*/
	public java.sql.Timestamp last_update_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
	/**此节点被添加的顺序，之前添加为BEFORE,之后添加为AFTER，平行添加为PARALLEL，如果不是被添加节点则该属性为空*/
	public String added_order;
}
