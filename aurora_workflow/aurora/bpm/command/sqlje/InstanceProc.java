package aurora.bpm.command.sqlje;

import uncertain.composite.*;
import aurora.bpm.command.sqlje.*;
import aurora.bpm.command.beans.*;
import java.sql.*;
import java.util.List;
import aurora.sqlje.exception.*;
import java.util.Map;
import aurora.sqlje.core.*;

public class InstanceProc implements aurora.sqlje.core.ISqlCallEnabled {
	public Long create(String code, String version, Long parent_id,
			Long instance_param) throws Exception {
		BpmnProcessInstance bpi = new BpmnProcessInstance();
		bpi.status = "RUNNING";
		bpi.process_code = code;
		bpi.process_version = version;
		bpi.parent_id = parent_id;
		bpi.instance_param = instance_param;
		$sql.insert(bpi);
		BpmnDocumentReference doc_ref = null;
		String data_object = "{}";
		try {
			String _$sqlje_sql_gen8 = "select df.*\n\t\t\t\tfrom bpmn_document_reference df,bpmn_process_define pd\n\t\t\t\twhere df.category_id = pd.category_id\n\t\t\t\tand pd.process_code=?\n\t\t\t\tand pd.process_version=?";
			PreparedStatement _$sqlje_ps_gen7 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen8);
			_$sqlje_ps_gen7.setString(1, code);
			_$sqlje_ps_gen7.setString(2, version);
			$sql.clear();
			_$sqlje_ps_gen7.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen7.getUpdateCount();
			ResultSet _$sqlje_rs_gen0 = _$sqlje_ps_gen7.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen0);
			getSqlCallStack().push(_$sqlje_ps_gen7);
			doc_ref = DataTransfer.transfer1(BpmnDocumentReference.class,
					_$sqlje_rs_gen0);
			StringBuilder _$sqlje_sql_gen10 = new StringBuilder();
			_$sqlje_sql_gen10.append("select * from ");
			_$sqlje_sql_gen10.append("(" + doc_ref.ref_detail + ")");
			_$sqlje_sql_gen10.append(" z\n\t\t\t\t\t\t\t\t\t\t\twhere ");
			_$sqlje_sql_gen10.append(doc_ref.ref_id_column_name);
			_$sqlje_sql_gen10.append(" = ?");
			String _$sqlje_sql_gen11 = _$sqlje_sql_gen10.toString();
			PreparedStatement _$sqlje_ps_gen9 = getSqlCallStack()
					.getCurrentConnection().prepareStatement(_$sqlje_sql_gen11);
			_$sqlje_ps_gen9.setLong(1, instance_param);
			$sql.clear();
			_$sqlje_ps_gen9.execute();
			$sql.UPDATECOUNT = _$sqlje_ps_gen9.getUpdateCount();
			ResultSet _$sqlje_rs_gen1 = _$sqlje_ps_gen9.getResultSet();
			getSqlCallStack().push(_$sqlje_rs_gen1);
			getSqlCallStack().push(_$sqlje_ps_gen9);
			CompositeMap process_param = DataTransfer.transfer1(
					CompositeMap.class, _$sqlje_rs_gen1);
			data_object = uncertain.composite.JSONAdaptor.toJSONObject(
					process_param).toString();
		} catch (NoDataFoundException e) {
		}
		BpmnProcessData data = new BpmnProcessData();
		data.instance_id = bpi.instance_id;
		data.data_object = data_object;
		$sql.insert(data);
		return bpi.instance_id;
	}

	public BpmnProcessInstance query(Long instance_id) throws Exception {
		String _$sqlje_sql_gen13 = "select * from bpmn_process_instance where instance_id=?";
		PreparedStatement _$sqlje_ps_gen12 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen13);
		_$sqlje_ps_gen12.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen12.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen12.getUpdateCount();
		ResultSet _$sqlje_rs_gen2 = _$sqlje_ps_gen12.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen2);
		getSqlCallStack().push(_$sqlje_ps_gen12);
		BpmnProcessInstance bpi = DataTransfer.transfer1(
				BpmnProcessInstance.class, _$sqlje_rs_gen2);
		return bpi;
	}

	public BpmnProcessData getProcessData(Long instance_id) throws Exception {
		String _$sqlje_sql_gen15 = "select * from bpmn_process_data where instance_id = ?";
		PreparedStatement _$sqlje_ps_gen14 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen15);
		_$sqlje_ps_gen14.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen14.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen14.getUpdateCount();
		ResultSet _$sqlje_rs_gen3 = _$sqlje_ps_gen14.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen3);
		getSqlCallStack().push(_$sqlje_ps_gen14);
		BpmnProcessData data = DataTransfer.transfer1(BpmnProcessData.class,
				_$sqlje_rs_gen3);
		return data;
	}

	public void saveDataObject(BpmnProcessData data) throws Exception {
		String _$sqlje_sql_gen17 = "update bpmn_process_data set data_object = ? where instance_id = ?";
		PreparedStatement _$sqlje_ps_gen16 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen17);
		_$sqlje_ps_gen16.setObject(1, data.data_object);
		_$sqlje_ps_gen16.setObject(2, data.instance_id);
		$sql.clear();
		_$sqlje_ps_gen16.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen16.getUpdateCount();
		ResultSet _$sqlje_rs_gen4 = _$sqlje_ps_gen16.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen4);
		getSqlCallStack().push(_$sqlje_ps_gen16);
	}

	public void finish(Long instance_id) throws Exception {
		String _$sqlje_sql_gen19 = "update bpmn_process_instance set status='FINISH' where instance_id=?";
		PreparedStatement _$sqlje_ps_gen18 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen19);
		_$sqlje_ps_gen18.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen18.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen18.getUpdateCount();
		ResultSet _$sqlje_rs_gen5 = _$sqlje_ps_gen18.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen5);
		getSqlCallStack().push(_$sqlje_ps_gen18);
		String _$sqlje_sql_gen21 = "delete from bpmn_process_token where instance_id=?";
		PreparedStatement _$sqlje_ps_gen20 = getSqlCallStack()
				.getCurrentConnection().prepareStatement(_$sqlje_sql_gen21);
		_$sqlje_ps_gen20.setLong(1, instance_id);
		$sql.clear();
		_$sqlje_ps_gen20.execute();
		$sql.UPDATECOUNT = _$sqlje_ps_gen20.getUpdateCount();
		ResultSet _$sqlje_rs_gen6 = _$sqlje_ps_gen20.getResultSet();
		getSqlCallStack().push(_$sqlje_rs_gen6);
		getSqlCallStack().push(_$sqlje_ps_gen20);
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
