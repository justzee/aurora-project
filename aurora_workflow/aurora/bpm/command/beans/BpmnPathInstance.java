package aurora.bpm.command.beans;

import aurora.sqlje.core.annotation.PK;
import aurora.sqlje.core.annotation.Table;

@Table(name = "bpmn_path_instance", stdwho = false)
public class BpmnPathInstance {
	@PK
	public Long path_id;
	public Long instance_id;
	public String status;
	public String prev_node;
	public String current_node;
	public String node_id;

}
