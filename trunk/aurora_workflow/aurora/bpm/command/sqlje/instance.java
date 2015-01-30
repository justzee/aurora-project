package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import aurora.bpm.command.sqlje.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class instance implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(String code, String version, Long parent_id,
			Long instance_param) throws Exception {
		BpmnProcessInstance bpi = new BpmnProcessInstance();
		bpi.status = "RUNNING";
		bpi.process_code = code;
		bpi.process_version = version;
		bpi.parent_id = parent_id;
		bpi.instance_param = instance_param;
		$sql.insert(bpi);
		BpmnProcessData data = new BpmnProcessData();
		data.instance_id = bpi.instance_id;
		data.data_object = "{}";
		$sql.insert(data);
		return bpi.instance_id;
	}

	public BpmnProcessInstance query(Long instance_id) throws Exception {
		String _$sqlje_sql_gen5 = "select * from bpmn_process_instance where instance_id=?";
		PreparedStatement _$sqlje_ps_gen4 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen5);
		_$sqlje_ps_gen4.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen4.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen4.getUpdateCount();
		ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen4.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen0);
		getSqlCallStack().push(_$sqlje_ps_gen4);
		BpmnProcessInstance bpi = DataTransfer.transfer1(
				BpmnProcessInstance.class, _$sqlje_rs_gen0);
		return bpi;
	}

	public BpmnProcessData getProcessData(Long instance_id) throws Exception {
		String _$sqlje_sql_gen7 = "select * from bpmn_process_data where instance_id = ?";
		PreparedStatement _$sqlje_ps_gen6 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen7);
		_$sqlje_ps_gen6.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen6.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen6.getUpdateCount();
		ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen6.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen1);
		getSqlCallStack().push(_$sqlje_ps_gen6);
		BpmnProcessData data = DataTransfer.transfer1(BpmnProcessData.class,
				_$sqlje_rs_gen1);
		return data;
	}

	public void saveDataObject(BpmnProcessData data) throws Exception {
		String _$sqlje_sql_gen9 = "update bpmn_process_data set data_object = ? where instance_id = ?";
		PreparedStatement _$sqlje_ps_gen8 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen9);
		_$sqlje_ps_gen8.setObject(1, data.data_object);
		_$sqlje_ps_gen8.setObject(2, data.instance_id);
		$sql.clear();
		_$sqlje_ps_gen8.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen8.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen8.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen8);
	}

	public void finish(Long instance_id) throws Exception {
		String _$sqlje_sql_gen11 = "update bpmn_process_instance set status='FINISH' where instance_id=?";
		PreparedStatement _$sqlje_ps_gen10 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen11);
		_$sqlje_ps_gen10.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen10.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen10.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen10.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen10);
	}

	protected aurora.sqlje.core.ISqlCallStack _$sqlje_sqlCallStack = null;
	protected aurora.sqlje.core.IInstanceManager _$sqlje_instanceManager = null;
	protected SqlFlag $sql = new SqlFlag(this);

	public aurora.sqlje.core.ISqlCallStack getSqlCallStack() {
		return _$sqlje_sqlCallStack;
	}

	public void _$setSqlCallStack(aurora.sqlje.core.ISqlCallStack args0) {
		_$sqlje_sqlCallStack = args0;
	}

	public aurora.sqlje.core.IInstanceManager getInstanceManager() {
		return _$sqlje_instanceManager;
	}

	public void _$setInstanceManager(aurora.sqlje.core.IInstanceManager args0) {
		_$sqlje_instanceManager = args0;
	}
}
