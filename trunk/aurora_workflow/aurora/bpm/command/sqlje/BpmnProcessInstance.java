package aurora.bpm.command.sqlje;

import aurora.sqlje.core.annotation.PK;
import aurora.sqlje.core.annotation.Table;

@Table(name="bpmn_process_instance",stdwho=false)
public class BpmnProcessInstance {
	@PK
	public Long instance_id;
	public String status;
	public Long parent_id;
	public String process_code;
	public String process_version;
	public String description;
	public Long instance_param;
}
