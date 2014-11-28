package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.io.Reader;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class instance implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(String code, String version) throws Exception {
		CompositeMap data = new CompositeMap();
		data.put("status", "RUNNING");
		data.put("process_code", code);
		data.put("process_version", version);
		new aurora.sqlje.core.database.MysqlInsert(getSqlCallStack(), data,
				"bpmn_process_instance", "instance_id").insert();
		return data.getLong("instance_id");
	}

	public CompositeMap query(Long instance_id) throws Exception {
		PreparedStatement _$sqlje_ps_gen1 = getSqlCallStack()
				.getCurrentConnection()
				.prepareStatement(
						"select * from bpm_proess_instance where isntance_id=?");
		_$sqlje_ps_gen1.setLong(1, instance_id);
		_$sqlje_ps_gen1.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen1.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen1.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen1);
		CompositeMap map = DataTransfer.transfer1(CompositeMap.class,
				_$sqlje_rs_gen0);
		return map;
	}

	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected SqlFlag $sql = new SqlFlag();

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
