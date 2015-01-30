package aurora.bpm.command.sqlje;

import aurora.sqlje.core.annotation.*;

@Table(name = "BPMN_PROCESS_DATA", stdwho = false)
public class BpmnProcessData {
	@PK
	public Long data_id;
	public Long instance_id;
	public String data_object;
}
