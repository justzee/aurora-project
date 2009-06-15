/*
 * Created on 2009-5-7
 */
package org.lwap.siebelplugin;

import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.lwap.siebelplugin.FieldMapping;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;

import com.sap.mw.jco.IMetaData;
import com.sap.mw.jco.JCO;
import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelException;

public class Buscomp {

	public static final String IMPORT = "import";
	public static final String EXPORT = "export";

	/** Name of compoment */
	public String Name;

	/**
	 * Where to put export table. If table is fetched as CompositeMap, this is a
	 * 'map path', such as '/model/result/list'; if table is fetched as Array,
	 * this is a 'attribute path', such as '/model/result/@field'
	 */
	public String Target;

	/**
	 * Type of table, 'export' or 'import'
	 */
	public String Type = EXPORT;

	/**
	 * Source field path for import table
	 */
	public String Source_field;

	/**
	 * For export table, how data is fetched. 'map': the result is fetched as
	 * CompositeMap 'array': the result is fetched as java.sql.Array
	 */
	public String Fetch_type = "map";

	/**
	 * For fetch_type 'array': Name of PL/SQL collection type
	 */
	public String Collection_type;

	/**
	 * For fetch_type 'array': Name of PL/SQL struct type for the array element
	 */
	public String Struct_type;

	Logger logger;

	boolean Dump = false;

	FieldMapping[] field_mappings;
	HashMap source_map;
	HashMap name_map;

	public Buscomp(Logger logger) {
		this.logger = logger;
	}

	public CompositeMap toCompositeMap() {
		CompositeMap table = new CompositeMap("siebel",
				"org.lwap.siebelplugin", "buscomp");
		table.put("name", Name);
		table.put("type", Type);
		table.put("target", Target);
		table.put("source_field", Source_field);
		table.put("fetch_type", Fetch_type);
		table.put("collection_type", Collection_type);
		table.put("struct_type", Struct_type);
		if (field_mappings != null) {
			CompositeMap mappings = table.createChild("field-mappings");
			for (int i = 0; i < field_mappings.length; i++) {
				CompositeMap mapping = new CompositeMap("field-mapping");
				mapping.put("name", field_mappings[i].Name);
				mapping.put("source_name", field_mappings[i].Source_name);
				mappings.addChild(mapping);
			}
		}
		return table;
	}

	public void setFieldMappings(FieldMapping[] m) {
		System.out.println("&&&&&&&&&&&&&&&&&&¹þ¹þ");
		field_mappings = m;
		source_map = new HashMap();
		name_map = new HashMap();
		for (int i = 0; i < m.length; i++) {
			if (m[i].Name == null)
				throw new ConfigurationError(
						"Must set 'name' for <field-mapping>");
			if (m[i].Source_name == null)
				throw new ConfigurationError(
						"Must set 'source_name' for <field-mapping>");
			name_map.put(m[i].Name, m[i]);
			source_map.put(m[i].Source_name, m[i]);
		}
	}

	public FieldMapping[] getFieldMappings() {
		return field_mappings;
	}

	public boolean isImport() {
		return IMPORT.equalsIgnoreCase(Type);
	}

	public boolean isFetchTypeMap() {
		return "map".equalsIgnoreCase(Fetch_type);
	}

	public boolean isFetchTypeArray() {
		return "array".equalsIgnoreCase(Fetch_type);
	}

	/**
	 * Fill a JCO.Table with data from an Oracle PL/SQL Array
	 * 
	 * @param table
	 * @param array
	 * @throws SQLException
	 */
	public void insertBusComp(SiebelBusComp busComp, Array array) throws SQLException {
		// IMetaData tmd = table.getMetaData();

		if (!(array instanceof ARRAY)) {
			throw new IllegalArgumentException(
					"Currently only oracle table is surpported");
		}
		Object[] items = (Object[]) array.getArray();
		if (items == null || !(items.length > 0)) {
			if (Dump) {
				logger.info("No rows fetched in PL/SQL table");
			}
			return;
		}
		try {
			busComp.newRecord(true);
		
//		table.appendRows(items.length);
		// table.nextRow();
		ResultSetMetaData smd = null;
		if (Dump) {
			logger.info("Appending " + items.length + " rows to ABAP table "
					+ Name);
		}
		for (int i = 0; i < items.length; i++) {
			STRUCT s = (STRUCT) items[i];
			if (smd == null)
				smd = s.getDescriptor().getMetaData();
			Object[] values = s.getAttributes();
			for (int n = 0; n < values.length; n++) {
				String source_name = smd.getColumnName(n + 1);
				String field_name = source_name;
				FieldMapping mapping = (FieldMapping) source_map
						.get(source_name);
				if (mapping != null)
					field_name = mapping.Name;
//				table.setValue(, field_name);
				busComp.setFieldValue(field_name, (String)values[n]);
				busComp.writeRecord();
				if (Dump) {
					logger.info(field_name + " -> " + values[n]);
				}
			}
			busComp.newRecord(true);
			logger.info("================ end line " + i
					+ "=====================");
		}
		if (Dump) {
			logger.info("\r\nTable transfered");
		}
		} catch (SiebelException e) {
			// TODO Auto-generated catch block
			logger.info(e.getErrorMessage()+" "+e.getDetailedMessage());
		}
	}

	/**
	 * Fill a CompositeMap with records fetched from JCO.Table
	 * 
	 * @param records
	 *            An instance of JCO.Table containing data
	 * @param result
	 *            Target CompositeMap to be filled with, each record in
	 *            JCO.Table will be created as a child record of CompositeMap
	 * @return filled CompositeMap
	 */
	public CompositeMap fillCompositeMap(SiebelBusComp busComp,
			CompositeMap result) {
		boolean hasRecord = false;
		try {
			hasRecord = busComp.firstRecord();
			Set fieldSet = name_map.keySet();
			int fieldCount = fieldSet.size();
			Iterator fieldIterator = fieldSet.iterator();
			while (hasRecord) {
				// loop return table
				// create CompositeMap record
				CompositeMap item = new CompositeMap((int) (fieldCount * 1.5));
				item.setName("record");
				// put all fields in table into CompositeMap
				fieldIterator = fieldSet.iterator();
				while (fieldIterator.hasNext()) {
					String field = (String) fieldIterator.next();
//					System.out.println("*************** field  "+field);
					item.put(field, busComp.getFieldValue(field));
//					System.out.println("*************** value  "+busComp.getFieldValue(field));
				}
				result.addChild(item);

				hasRecord = busComp.nextRecord();
			}
		} catch (SiebelException e) {
			logger.info("busComp has no more record");
		}
		return result;
	}
	
//	public String updateBusComp(SiebelBusComp busComp,Array array){
//		String return_message = "update successful!";
//
//		if (!(array instanceof ARRAY)) {
//			throw new IllegalArgumentException(
//					"Currently only oracle table is surpported");
//		}
//		Object[] items = (Object[]) array.getArray();
//		ResultSetMetaData smd = null;
//		if (items == null || !(items.length > 0)) {
//			if (Dump) {
//				logger.info("No rows fetched in PL/SQL table");
//			}
//			return return_message;
//		}
//		for (int i = 0; i < items.length; i++) {
//			STRUCT s = (STRUCT) items[i];
//			if (smd == null)
//				smd = s.getDescriptor().getMetaData();
//			Object[] values = s.getAttributes();
//			for (int n = 0; n < values.length; n++) {
//				String source_name = smd.getColumnName(n + 1);
//				String field_name = source_name;
//				FieldMapping mapping = (FieldMapping) source_map
//						.get(source_name);
//				if (mapping != null)
//					field_name = mapping.Name;
////				table.setValue(, field_name);
//				busComp.setFieldValue(field_name, (String)values[n]);
//				busComp.writeRecord();
//				if (Dump) {
//					logger.info(field_name + " -> " + values[n]);
//				}
//			}
//
//		
//
//		boolean hasRecord = false;
//		try {
//			hasRecord = busComp.firstRecord();
//			Set sourceFieldSet = source_map.keySet();
//			Set fieldSet = name_map.keySet();
//			int fieldCount = sourceFieldSet.size();
//			Iterator sourceFieldIterator = sourceFieldSet.iterator();
//			Iterator fieldIterator = fieldSet.iterator();
//			while (hasRecord) {
//				// loop return table
//				// create CompositeMap record
//				CompositeMap item = new CompositeMap((int) (fieldCount * 1.5));
//				item.setName("record");
//				// put all fields in table into CompositeMap
//				sourceFieldIterator = sourceFieldSet.iterator();
//				while (sourceFieldIterator.hasNext()) {
//					String sourceField = (String) sourceFieldIterator.next();
//					String field = (String) fieldIterator.next();
//					String value = (String)context.getObject(sourceField);
//					System.out.println(" update field "+field+"value "+value);
//					busComp.setFieldValue(field, value);
//					busComp.writeRecord();
//				}
//
//				hasRecord = busComp.nextRecord();
//			}
//		} catch (SiebelException e) {
//			logger.info(e.getDetailedMessage()+e.getErrorMessage());
//		}
//		logger.info(return_message);
//		return return_message;
//		
//	}
	
	public String updateBusComp(SiebelBusComp busComp,CompositeMap context){
		String return_message = "update successful!";
		boolean hasRecord = false;
		try {
			hasRecord = busComp.firstRecord();
			Set sourceFieldSet = source_map.keySet();
			Set fieldSet = name_map.keySet();
			int fieldCount = sourceFieldSet.size();
			Iterator sourceFieldIterator = sourceFieldSet.iterator();
			Iterator fieldIterator = fieldSet.iterator();
			while (hasRecord) {
				// loop return table
				// create CompositeMap record
				CompositeMap item = new CompositeMap((int) (fieldCount * 1.5));
				item.setName("record");
				// put all fields in table into CompositeMap
				sourceFieldIterator = sourceFieldSet.iterator();
				while (sourceFieldIterator.hasNext()) {
					String sourceField = (String) sourceFieldIterator.next();
					String field = (String) fieldIterator.next();
					String value = (String)context.getObject(sourceField);
					System.out.println(" update field "+field+"value "+value);
					busComp.setFieldValue(field, value);
					busComp.writeRecord();
				}

				hasRecord = busComp.nextRecord();
			}
		} catch (SiebelException e) {
			logger.info(e.getDetailedMessage()+e.getErrorMessage());
		}
		logger.info(return_message);
		return return_message;
		
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
			struct_map.put(md.getColumnName(i),
					new Integer(i - 1));
		// Array of pl/sql struct field id indexed by ABAP table field id
		// ids[sap_field_id] = Integer<pl/sql_field_id>
		Set fieldSet = name_map.keySet();
		int fieldCount = fieldSet.size();
		Iterator fieldIterator = fieldSet.iterator();

		Integer[] ids = new Integer[fieldCount];
		String[] fields = new String[fieldCount];
		int i = 0;
		while (fieldIterator.hasNext()) {
			String fname = (String) fieldIterator.next();
			FieldMapping mapping = (FieldMapping) name_map.get(fname);
			if (mapping != null)
				fname = mapping.Source_name;
			Integer id = (Integer) struct_map.get(fname);
			ids[i++] = id;
			fields[i++] = fname;
		}
		List elements = null;
		try {
			// Create Object array to hold each record in ABAP table
			busComp.firstRecord();

			elements = new ArrayList();
			fieldIterator = fieldSet.iterator();
			// Object[] elements = new Object[records.getNumRows()];
			while (fieldIterator.hasNext()) {
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
			logger.info(e.getErrorMessage());
		}
		ARRAY result = new ARRAY(adesc, conn, elements.toArray());

		return result;
	}

}
