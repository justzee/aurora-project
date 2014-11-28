package aurora.bpm.command;

import aurora.bpm.engine.ExecutorContext;
import aurora.sqlje.core.ISqlCallStack;

public interface ICommandExecutor {
	void init(ExecutorContext context);

	ExecutorContext getExecutorContext();

	/**
	 * execute the command in a new transaction(SqlCallStack)
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	void execute(Command cmd) throws Exception;

	/**
	 * execute the command with a pre-created SqlCallStack,no new transaction
	 * create
	 * 
	 * @param callStack
	 * @param cmd
	 * @throws Exception
	 */
	void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception;
}
