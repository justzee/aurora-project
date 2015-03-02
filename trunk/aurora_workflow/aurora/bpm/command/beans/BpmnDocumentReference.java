package aurora.bpm.command.beans;
import aurora.sqlje.core.annotation.*;
@Table(name="BPMN_DOCUMENT_REFERENCE",stdwho=false)
public class BpmnDocumentReference
{
	@PK
	public Long reference_id;
	/**工作流类型*/
	public String category_id;
	/**描述ID*/
	public String description_id;
	/**单据表名*/
	public String document_table_name;
	/**引用单据表ID列名*/
	public String ref_id_column_name;
	/**引用单据号列名*/
	public String ref_num_column_name;
	/**引用公司列名*/
	public String ref_company_column_name;
	/**引用创建用户列名*/
	public String ref_created_column_name;
	/**配置SQL*/
	public String ref_detail;
	/**是否系统创建标志*/
	public String sys_flag;
	/**创建用户ID*/
	public Long created_by;
	/**创建日期*/
	public java.sql.Timestamp creation_date;
	/**最后更新用户ID*/
	public Long last_updated_by;
	/**最后更新日期*/
	public java.sql.Timestamp last_update_date;
}
