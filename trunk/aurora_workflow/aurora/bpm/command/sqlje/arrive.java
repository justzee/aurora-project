package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class arrive implements aurora.sqlje.core.ISqlCallEnabled {
	public void arrive(Long instance_id, Long path_id, Long node_id)
			throws Exception {
	}

	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}
}
