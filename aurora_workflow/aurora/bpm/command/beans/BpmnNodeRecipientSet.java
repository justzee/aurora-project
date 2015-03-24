package aurora.bpm.command.beans;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_NODE_RECIPIENT_SET",stdwho=false)
public class BpmnNodeRecipientSet
{
	/**RECIPIENT_SET_ID*/
	@PK
	public Long recipient_set_id;
	/**工作流节点ID*/
	public Long usertask_id;
	/**规则CODE*/
	public String rule_code;
	/**序号*/
	public Long rule_sequence;
	/**审批顺序*/
	public Long recipient_sequence;
	/**参数1值*/
	public String parameter_1_value;
	/**参数1描述*/
	public String parameter_1_desc;
	/**参数2值*/
	public String parameter_2_value;
	/**参数2描述*/
	public String parameter_2_desc;
	/**参数3值*/
	public String parameter_3_value;
	/**参数3描述*/
	public String parameter_3_desc;
	/**参数4值*/
	public String parameter_4_value;
	/**参数4描述*/
	public String parameter_4_desc;
	/**创建用户ID*/
	public Long created_by;
	/**创建日期*/
	public java.sql.Date creation_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
	/**最后更新日期*/
	public java.sql.Date last_update_date;
}
