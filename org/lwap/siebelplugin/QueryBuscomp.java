/** query Siebel BusComp
 *  Created on 2009-5-7
 */
package org.lwap.siebelplugin;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;

import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;

public class QueryBuscomp extends AbstractEntry {

	SiebelInstance siebelInstance;

	public String Bo_name;
	public String Bc_name;
	public int Viewmode = -1;
	public String Select_fields;
	public Parameter[] Parameters;
	public String Sort_spec;

	public String Fetch_type = "map";
	/**
	 * For fetch_type 'array': Name of PL/SQL collection type
	 */
	public String Collection_type;

	/**
	 * For fetch_type 'array': Name of PL/SQL struct type for the array element
	 */
	public String Struct_type;
	public String ElementName;
	public String Target;
	
	public int Fetch_max_num = -1; 

//	Logger mLogger;
	ILogger mLogger;
//	ILogger mErrorLogger;

	public QueryBuscomp(SiebelInstance si) {
		siebelInstance = si;
	}

	public String toString() {
		CompositeMap invoke = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "query-buscomp");
		invoke.put("bo_name", Bo_name);
		invoke.put("bc_name", Bc_name);
		invoke.put("viewmode", Integer.toString(Viewmode));
		invoke.put("select_fields", Select_fields);
		invoke.put("sort_spec", Sort_spec);
		invoke.put("fetch_type", Fetch_type);
		invoke.put("struct_type", Struct_type);
		invoke.put("target", Target);
		invoke.put("elementName", ElementName);

		if (Parameters != null) {
			CompositeMap params = invoke.createChild("parameters");
			for (int i = 0; i < Parameters.length; i++)
				params.addChild(Parameters[i].toCompositeMap());
		}
		return invoke.toXML();
	}

	public void run(ProcedureRunner runner) throws Exception {
		CompositeMap context = runner.getContext();
		initLogger(context);
		mLogger.log(Level.INFO, "query-buscomp");
		mLogger.log(Level.INFO, "===================================");
		mLogger.log(Level.CONFIG, toString());


		MainService service = MainService.getServiceInstance(context.getRoot());
		CompositeMap target = null;
		CompositeMap model = null;

		if (service != null)
			model = service.getModel();
		else
			model = context.getRoot().getChild("model");
		if (model == null)
			model = context.getRoot().createChild("model");
		if (Bo_name == null)
			throw new ConfigurationError(
					"Must set 'bo_name' attribute for query-buscomp");
		if (Bc_name == null)
			throw new ConfigurationError(
					"Must set 'bc_name' attribute for query-buscomp");
		if (Select_fields == null)
			throw new ConfigurationError(
					"Must set 'select_fields' attribute for query-buscomp");
		if (Target != null && ElementName != null)
			throw new ConfigurationError(
					"Can not set target and elementname attribute for query-buscomp at the same time");
		if (Target == null && ElementName == null)
			throw new ConfigurationError(
					"Must set target or elementname attribute for query-buscomp ");
		if (ElementName != null) {
			String t = TextParser.parse(ElementName, context);
			target = (CompositeMap) model.getObject(t);
			if (target == null)
				target = model.createChildByTag(t);
		}
		if (Target != null) {
			String t = TextParser.parse(Target, context);
			target = (CompositeMap) model.getObject(t);
			if (target == null)
				target = model.createChildByTag(t);
		}
		CompositeMap params =service.getParameters();
		CompositeMap siebel_login = new CompositeMap();
		
		service.databaseAccess("Sieble_Login.data", params, siebel_login);
		
		String USER_NAME = (String)siebel_login.getObject("siebel-login/@USER_NAME");
		String PWD = (String)siebel_login.getObject("siebel-login/@PWD");
		
		long time = System.currentTimeMillis();
		SiebelDataBean siebelDataBean = siebelInstance.getClient(USER_NAME,PWD,time);
		SiebelBusObject busObject = null;
		SiebelBusComp busComp = null;
		try {
			busObject = siebelDataBean.getBusObject(Bo_name);
			mLogger.log(Level.CONFIG, "busObject : " + Bo_name);
			// if the function definition was found in backend system
			if (busObject == null) {
				throw new IllegalArgumentException("busObject '" + Bo_name
						+ "' not found in Siebel system.");
			}
			busComp = busObject.getBusComp(Bc_name);
			mLogger.log(Level.CONFIG, "busComp : " + Bc_name);

			if (busComp == null) {
				throw new IllegalArgumentException("busComp '" + Bc_name
						+ "' not found in busObject " + Bo_name + ".");
			}
			if (Viewmode != -1) {
				busComp.setViewMode(Viewmode);
				mLogger.log(Level.CONFIG, "viewmode : " + Viewmode);
			}

			busComp.clearToQuery();

			if (Parameters != null)
				for (int i = 0; i < Parameters.length; i++) {
					Parameter param = Parameters[i];
					if (param.Import != null
							&& param.Import.toLowerCase().equals("true")) {
						Object o = param.Source_field == null ? param.Value
								: context.getObject(param.Source_field);
						String value = o == null ? "" : o.toString();
						busComp.setSearchSpec(param.Name, value);
						mLogger.log(Level.CONFIG, "parameter " + param.Name
								+ " -> " + value);
					}
				}

			String[] fields = Select_fields.split(",");
			for (int i = 0; i < fields.length; i++) {
				busComp.activateField(fields[i]);
				mLogger.log(Level.CONFIG, "activateField: " + fields[i]);
			}
			if (Sort_spec != null) {
				busComp.setSortSpec(Sort_spec);
				mLogger.log(Level.CONFIG, "sort spec: " + Sort_spec);
			}

			busComp.executeQuery2(true, true);

			String return_name = Target != null?Target:ElementName;
			if (isFetchTypeMap()) {
				fillCompositeMap(busComp, target);
				int rc = 0;
				if (target.getChilds() != null)
					rc = target.getChilds().size();
				mLogger.log(Level.INFO, "loading export busComp " + Bc_name
						+ " into path '" + return_name + "', total " + rc
						+ " record(s)");
				// System.out.println(target.toXML());
			}
			// Fetch as Array
			else if (isFetchTypeArray()) {
				Connection conn = MainService.getConnection(context);
				Array array = fillArray(busComp, conn);
				context.putObject(return_name, array, true);
				int rc = 0;
				Object[] r = (Object[]) array.getArray();
				if (r != null)
					rc = r.length;
				mLogger.log(Level.INFO, "loading export table " + Bc_name
						+ " as " + array + ", total " + rc + " record(s)");
			} else
				throw new ConfigurationError(
						"Unknown fetch_type for export table:" + Fetch_type);
			mLogger.log(Level.INFO, "queryBuscomp invoke finished");

		} catch (SiebelException ex) {
			throw new Exception("error when query siebel buscomp:"
					+ ex.getDetailedMessage(), ex);
		} finally {
			busComp.release();
			busObject.release();
			mLogger.log(Level.FINE, "Siebel instance is released.");
			siebelInstance.release(USER_NAME,time);

		}

	}

	public boolean isFetchTypeMap() {
		return "map".equalsIgnoreCase(Fetch_type);
	}

	public boolean isFetchTypeArray() {
		return "array".equalsIgnoreCase(Fetch_type);
	}

	public CompositeMap fillCompositeMap(SiebelBusComp busComp,
			CompositeMap result) throws Exception{
		boolean hasRecord = false;
		String[] fieldSet = Select_fields.split(",");
		int fieldCount = fieldSet.length;
//		try {
			try {
				hasRecord = busComp.firstRecord();
			} catch (SiebelException e1) {
				mLogger.log(Level.INFO, "queryBuscomp has no record");
			}
			result.put("count_flag", "-");
			if (hasRecord && ElementName != null) {
				for (int i = 0; i < fieldSet.length; i++) {
					String field = fieldSet[i];
					try {
						result.put(field, busComp.getFieldValue(field));
					} catch (SiebelException ex) {
						throw new Exception("error when query siebel buscomp:"
								+ ex.getDetailedMessage(), ex);
					}

				}
				hasRecord = false;
			}
			while (hasRecord) {
				// loop return table
				// create CompositeMap record
				CompositeMap item = new CompositeMap((int) (fieldCount * 1.5));
				item.setName("record");
				// put all fields in table into CompositeMap
				for (int i = 0; i < fieldSet.length; i++) {
					String field = fieldSet[i];
					try {
						item.put(field, busComp.getFieldValue(field));
					} catch (SiebelException ex) {
						throw new Exception("error when query siebel buscomp:"
								+ ex.getDetailedMessage(), ex);
					}
				}
				result.addChild(item);

				try {
					hasRecord = busComp.nextRecord();
					if((--Fetch_max_num)==0){
						if(hasRecord == false)
							result.put("count_flag", "=");
						else
							result.put("count_flag", "+");
						break;
					}
				} catch (SiebelException e) {
					mLogger.log(Level.INFO, "queryBuscomp:this is last record");
				}
			}
		return result;
	}

	public Array fillArray(SiebelBusComp busComp, Connection conn)
			throws SQLException {
		if (Collection_type == null)
			throw new ConfigurationError(
					"Must set 'collection_type' to fetch table as pl/sql collection");
		if (Struct_type == null)
			throw new ConfigurationError(
					"Must set 'Struct_type' to fetch table as pl/sql collection");
		// Get pl/sql type descriptor
		ArrayDescriptor adesc = ArrayDescriptor.createDescriptor(
				Collection_type, conn);
		StructDescriptor sdesc = StructDescriptor.createDescriptor(Struct_type,
				conn);
		// construct a map of pl/sql struct field name -> its field id
		HashMap struct_map = new HashMap();
		ResultSetMetaData md = sdesc.getMetaData();
		int field_count = md.getColumnCount();
		for (int i = 1; i <= field_count; i++)
			struct_map.put(md.getColumnName(i), new Integer(i - 1));
		// Array of pl/sql struct field id indexed by ABAP table field id
		// ids[sap_field_id] = Integer<pl/sql_field_id>
		// Set fieldSet = name_map.keySet();
		String[] fieldSet = Select_fields.split(",");
		int fieldCount = fieldSet.length;
		// Iterator fieldIterator = fieldSet.iterator();

		Integer[] ids = new Integer[fieldCount];
		String[] fields = new String[fieldCount];

		for (int i = 0; i < fieldSet.length; i++) {
			String fname = fieldSet[i];
			Integer id = (Integer) struct_map.get(fname);
			ids[i] = id;
			fields[i] = fname;
		}
		List elements = null;
		try {
			// Create Object array to hold each record in ABAP table
			busComp.firstRecord();

			elements = new ArrayList();
			for (int i = 0; i < fieldSet.length; i++) {
				Object[] attribs = new Object[field_count];
				for (int c = 0; c < ids.length; c++) {
					if (ids[c] == null)
						continue;
					int id = ids[c].intValue();

					attribs[id] = busComp.getFieldValue(fields[c]);

				}
				STRUCT rec = new STRUCT(sdesc, conn, attribs);
				elements.add(rec);
			}
		} catch (SiebelException e) {
			mLogger.log(Level.CONFIG,e.getDetailedMessage());
		}
		ARRAY result = new ARRAY(adesc, conn, elements.toArray());

		return result;
	}

	public void initLogger(CompositeMap context) {
		CompositeMap m = context.getRoot();
		mLogger = LoggingContext.getLogger(m, SiebelInstance.LOGGING_TOPIC);
//		mLogger = Logger.getLogger(SiebelInstance.LOGGING_TOPIC);
//		mErrorLogger = LoggingContext.getErrorLogger(m);
	}

}
