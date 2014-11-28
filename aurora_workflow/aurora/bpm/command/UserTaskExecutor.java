package aurora.bpm.command;

import aurora.database.service.IDatabaseServiceFactory;

public class UserTaskExecutor extends AbstractCommandExecutor {
	public UserTaskExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
		// TODO Auto-generated constructor stub
	}

	public static final String TYPE = "USERTASK";

}
