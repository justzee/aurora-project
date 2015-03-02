package aurora.bpm.command.beans;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_RECIPIENT_RULES",stdwho=false)
public class BpmnRecipientRules
{
	@PK
	public Long rule_id;
	/**规则CODE*/
	public String rule_code;
	/**审批规则类型*/
	public String rule_type;
	/**审批规则名称*/
	public Long name_id;
	/**描述ID*/
	public Long description_id;
	/**执行过程*/
	public String procedure_name;
	/**系统创建标志*/
	public String sys_flag;
	/**SELECT：LOV；INPUT：textedit*/
	public String parameter_1_type;
	/**参数1描述*/
	public String parameter_1_desc;
	/**Lov组件文件名*/
	public String parameter_1_url;
	/**SELECT：LOV；INPUT：textedit*/
	public String parameter_2_type;
	/**参数2描述*/
	public String parameter_2_desc;
	/**Lov组件文件名*/
	public String parameter_2_url;
	/**SELECT：LOV；INPUT：textedit*/
	public String parameter_3_type;
	/**参数3描述*/
	public String parameter_3_desc;
	/**Lov组件文件名*/
	public String parameter_3_url;
	/**SELECT：LOV；INPUT：textedit*/
	public String parameter_4_type;
	/**参数4描述*/
	public String parameter_4_desc;
	/**Lov组件文件名*/
	public String parameter_4_url;
	/**创建用户ID*/
	public Long created_by;
	/**创建日期*/
	public java.sql.Timestamp creation_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
	/**最后更新日期*/
	public java.sql.Timestamp last_update_date;
}
