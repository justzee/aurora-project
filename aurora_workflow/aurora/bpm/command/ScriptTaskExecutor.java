package aurora.bpm.command;

import aurora.database.service.IDatabaseServiceFactory;

public class ScriptTaskExecutor extends AbstractCommandExecutor {
	public ScriptTaskExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
		// TODO Auto-generated constructor stub
	}

	public static final String TYPE = "SCRIPTTASK";

}
