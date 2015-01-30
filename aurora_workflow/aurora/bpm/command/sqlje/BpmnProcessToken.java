package aurora.bpm.command.sqlje;

import aurora.sqlje.core.annotation.PK;
import aurora.sqlje.core.annotation.Table;

@Table(name = "bpmn_process_token", stdwho = false)
public class BpmnProcessToken {
	@PK
	public Long token_id;
	public Long instance_id;
	public Long path_id;
	public String node_id;
}
