package aurora.bpm.command;

import org.eclipse.bpmn2.UserTask;

import aurora.bpm.command.beans.BpmnUsertaskNode;
import aurora.bpm.command.sqlje.ApproveProc;
import aurora.bpm.command.sqlje.UserTaskProc;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class UserTaskExecutor extends ApproveCmdExecutor {
	public UserTaskExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "USERTASK";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		Long instance_id = cmd.getOptions().getLong(INSTANCE_ID);
		Long user_id = cmd.getOptions().getLong(USER_ID);
		String node_id = cmd.getOptions().getString(NODE_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));
		UserTask currentNode = findFlowElementById(process, node_id,
				UserTask.class);
		String code = cmd.getOptions().getString(PROCESS_CODE);
		String version = cmd.getOptions().getString(PROCESS_VERSION);
		UserTaskProc ut = createProc(UserTaskProc.class, callStack);

		// get configuration for current user task node
		BpmnUsertaskNode userTaskSetting = ut.query(code, version, node_id);
		Long usertask_id = userTaskSetting.usertask_id;

		if (eq(userTaskSetting.recipient_type, 1L)) {
			// 自动审批通过
			// TODO create approve record
			ApproveProc appr = createProc(ApproveProc.class, callStack);
			appr.createApproveRecord(instance_id, usertask_id, null, "AGREE",
					"[自动审批通过]", null, null, user_id);
			callStack.getContextData().put(APPROVE_RESULT_PATH, "AGREE");
			gotoNext(currentNode, callStack, cmd, process, node_id, "AGREE");
			return;
		} else if (eq(userTaskSetting.recipient_type, -1L)) {
			// 自动审批拒绝
			// TODO create approve record
			ApproveProc appr = createProc(ApproveProc.class, callStack);
			appr.createApproveRecord(instance_id, usertask_id, null,
					"REJECT", "[自动审批拒绝]", null, null, user_id);
			callStack.getContextData().put(APPROVE_RESULT_PATH, "REJECT");
			gotoNext(currentNode, callStack, cmd, process, node_id, "REJECT");
			return;
		}

		ut.createInstanceNodeRule(instance_id, usertask_id, user_id);
		ut.createInstanceNodeHierarchy(instance_id, usertask_id, user_id);
		ut.createInstanceNodeRecipient(instance_id, usertask_id, user_id);

		ut.autoApprove(instance_id, usertask_id, user_id);

		if (ut.autoPass(instance_id, usertask_id, user_id)) {
			createOutgoingPath(callStack, currentNode, cmd);
		}

	}

}
