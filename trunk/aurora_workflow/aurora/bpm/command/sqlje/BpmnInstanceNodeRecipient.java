package aurora.bpm.command.sqlje;

import aurora.sqlje.core.annotation.PK;
import aurora.sqlje.core.annotation.Table;

@Table(name = "bpmn_instance_node_recipient", stdwho = false)
public class BpmnInstanceNodeRecipient {
	@PK
	public Long record_id;
	public Long instance_id;
	public String node_id;
	public Long user_id;
	public java.sql.Date date_limit;
	
	public java.sql.Date last_notify_date;
	public Long commision_by;
	public String commision_desc;
	public Long attachment;
}
