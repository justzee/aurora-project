package aurora.bpm.command.sqlje;

public interface IRecipientRule {
	void execute(String param1, String param2, String param3, String param4,
			Long rule_record_id) throws Exception;
}
