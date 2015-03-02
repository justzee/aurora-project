package aurora.bpm.command.beans;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_INSTANCE_NODE_RULE",stdwho=false)
public class BpmnInstanceNodeRule
{
	/**审批规则ID*/
	@PK
	public Long rule_record_id;
	/**工作流实例ID*/
	public Long instance_id;
	/**工作流节点ID*/
	public Long usertask_id;
	/**审批者来源类型*/
	public String recipient_type;
	/**审批者来源记录ID*/
	public Long recipient_set_id;
	/**序号*/
	public Long rule_sequence;
	/**规则顺序*/
	public Long recipient_sequence;
	/**参数1值*/
	public String parameter_1_value;
	/**参数2值*/
	public String parameter_2_value;
	/**参数3值*/
	public String parameter_3_value;
	/**参数4值*/
	public String parameter_4_value;
	/**规则CODE*/
	public String rule_code;
	/**审批者规则类型*/
	public String rule_type;
	/**创建日期*/
	public java.sql.Timestamp creation_date;
	/**创建用户ID*/
	public Long created_by;
	/**最后更新日期*/
	public java.sql.Timestamp last_update_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
}
