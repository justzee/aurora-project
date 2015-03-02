package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import aurora.bpm.command.beans.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class GatewayProc implements aurora.sqlje.core.ISqlCallEnabled {
	public boolean isWaitingForStart(Long instance_id, String node_id)
			throws Exception {
		try {
			String _$sqlje_sql_gen2 = "select wait_for_start from bpmn_complex_gateway_status where instance_id=? and node_id = ?";
			PreparedStatement _$sqlje_ps_gen1 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen2);
			_$sqlje_ps_gen1.setLong(1, instance_id);
			_$sqlje_ps_gen1.setString(2, node_id);
			$sql.clear();
			_$sqlje_ps_gen1.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen1.getUpdateCount();
			ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen1.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen0);
			getSqlCallStack().push(_$sqlje_ps_gen1);
			String waitfs = DataTransfer.transfer1(String.class,
					_$sqlje_rs_gen0);
			return "TRUE".equals(waitfs);
		} catch (NoDataFoundException e) {
			CompositeMap data = new CompositeMap();
			data.put("instance_id", instance_id);
			data.put("node_id", node_id);
			data.put("wait_for_start", "TRUE");
			$sql.insert(data, "bpmn_complex_gateway_status", "status_id");
		}
		return true;
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}
}
