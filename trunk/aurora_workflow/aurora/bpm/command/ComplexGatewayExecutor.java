package aurora.bpm.command;

import org.eclipse.bpmn2.ComplexGateway;
import org.eclipse.bpmn2.FormalExpression;

import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ComplexGatewayExecutor extends AbstractCommandExecutor {

	public ComplexGatewayExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		super.executeWithSqlCallStack(callStack, cmd);
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		ComplexGateway cg = findFlowElementById(process, node_id,
				ComplexGateway.class);
		
		FormalExpression exp=(FormalExpression) cg.getActivationCondition();
	}
	
	

}
