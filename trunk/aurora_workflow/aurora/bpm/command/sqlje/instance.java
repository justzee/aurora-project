package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import java.io.Reader;
import aurora.bpm.command.sqlje.*;
import java.sql.*;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class instance implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(String code, String version) throws Exception {
		BpmnProcessInstance bpi = new BpmnProcessInstance();
		bpi.status = "RUNNING";
		bpi.process_code = code;
		bpi.process_version = version;
		$sql.insert(bpi);
		return bpi.instance_id;
	}

	public BpmnProcessInstance query(Long instance_id) throws Exception {
		String _$sqlje_sql_gen3 = "select * from bpm_proess_instance where isntance_id=?";
		PreparedStatement _$sqlje_ps_gen2 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen3);
		_$sqlje_ps_gen2.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen2.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen2.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen2.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen2);
		BpmnProcessInstance bpi = DataTransfer.transfer1(
				BpmnProcessInstance.class, _$sqlje_rs_gen0);
		return bpi;
	}

	public void finish(Long instance_id) throws Exception {
		String _$sqlje_sql_gen5 = "update bpmn_process_instance set status='FINISH' where instance_id=?";
		PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen5);
		_$sqlje_ps_gen4.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen4.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen4.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen4);
	}

	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected SqlFlag $sql = new SqlFlag();

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
		$sql.setInstanceManager(_$sqlje_instanceManager);
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
		$sql.setSqlCallStack(_$sqlje_sqlCallStack);
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}
}
