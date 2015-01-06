package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import aurora.bpm.command.sqlje.*;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class user_task implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create_receipient(Long instance_id, String node_id, Long user_id)
			throws Exception {
		BpmnInstanceNodeRecipient binp = new BpmnInstanceNodeRecipient();
		binp.instance_id = instance_id;
		binp.node_id = node_id;
		binp.user_id = user_id;
		$sql.insert(binp);
		return binp.record_id;
	}

	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected SqlFlag $sql = new SqlFlag();

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
		$sql.setInstanceManager(_$sqlje_instanceManager);
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
		$sql.setSqlCallStack(_$sqlje_sqlCallStack);
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
