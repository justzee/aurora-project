package aurora.bpm.command;

import java.util.List;

import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.UserTask;

import uncertain.composite.CompositeMap;
import aurora.bpm.command.sqlje.BpmnUsertaskNode;
import aurora.bpm.command.sqlje.approve;
import aurora.bpm.script.BPMScriptEngine;
import aurora.database.service.IDatabaseServiceFactory;
import aurora.sqlje.core.ISqlCallStack;

public class ApproveCmdExecutor extends AbstractCommandExecutor {

	public static final String APPROVE_RESULT_PATH = "/"
			+ BPMScriptEngine.DATA_OBJECT + "/@approve_result";

	public ApproveCmdExecutor(IDatabaseServiceFactory dsf) {
		super(dsf);
	}

	public static final String TYPE = "APPROVE";

	@Override
	public void executeWithSqlCallStack(ISqlCallStack callStack, Command cmd)
			throws Exception {
		Long instance_id = cmd.getOptions().getLong(INSTANCE_ID);
		Long user_id = cmd.getOptions().getLong(USER_ID);
		org.eclipse.bpmn2.Process process = getProcess(loadDefinitions(cmd,
				callStack));

		// APPROVE 命令参数中没有node_id,仅有record_id(代办记录ID)
		String action_code = cmd.getOptions().getString("action_code");
		Long recipient_record = cmd.getOptions().getLong("record_id");
		String approve_content = cmd.getOptions().getString("approve_content");
		approve appr = createProc(approve.class, callStack);

		BpmnUsertaskNode bun = appr.queryByRecipientRecordId(recipient_record);
		String node_id = bun.node_id;
		cmd.getOptions().put(NODE_ID, node_id);
		UserTask userTask = findFlowElementById(process, node_id,
				UserTask.class);

		Long result = appr.approve(instance_id, recipient_record, user_id,
				action_code, approve_content);
		if (eq(result, 0L)) {
			System.out
					.printf("[usertask]%s approve not complete yet,wait ...\n",
							node_id);
			return;
		}
		// approve complete,the approve result can be accessed by
		// $data.approve_result
		callStack.getContextData().put(APPROVE_RESULT_PATH, result);

		gotoNext(userTask, callStack, cmd, process, node_id, result);

	}

	protected void gotoNext(UserTask userTask, ISqlCallStack callStack,
			Command cmd, org.eclipse.bpmn2.Process process, String node_id,
			Long result) throws Exception {
		List<SequenceFlow> outgoings = userTask.getOutgoing();
		// 如果下一个节点是选择网管,则交由选择网管进行判定
		if (outgoings.size() == 1
				&& outgoings.get(0).getTargetRef() instanceof ExclusiveGateway) {
			System.out.printf("[usertask]%s decision will be made by <%s>\n",
					node_id, outgoings.get(0).getTargetRef().getId());
			createPath(callStack, outgoings.get(0), cmd);
			return;
		}

		if (eq(result, 1L)) {
			System.out.printf("[usertask]%s approve PASS\n", node_id);
			createOutgoingPath(callStack, userTask, cmd);
		} else if (eq(result, -1L)) {
			System.out.printf("[usertask]%s approve FAILED, goto End-Event\n",
					node_id);
			List<org.eclipse.bpmn2.FlowElement> elements = process
					.getFlowElements();
			for (org.eclipse.bpmn2.FlowElement fe : elements) {
				if (fe instanceof EndEvent) {
					CompositeMap opts = cloneOptions(cmd);
					Command cmd2 = new Command(EndEvent.class.getSimpleName(),
							opts);
					dispatchCommand(callStack, cmd2);
					return;
				}
			}
		}
	}

}
