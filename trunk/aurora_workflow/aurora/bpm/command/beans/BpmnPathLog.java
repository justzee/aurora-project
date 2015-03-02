package aurora.bpm.command.beans;

import aurora.sqlje.core.annotation.*;

@Table(name = "BPMN_PATH_LOG", stdwho = false)
public class BpmnPathLog {
	@PK
	public Long log_id;
	public Long instance_id;
	public Long path_id;
	@InsertExpression("CURRENT_TIMESTAMP")
	public java.sql.Timestamp log_date;
	public String user_id;
	public String current_node;
	public String prev_node;
	public String event_type;
	public String log_content;
}
