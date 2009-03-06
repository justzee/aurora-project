/*
 * Created on 2007-7-6
 */
package org.lwap.sapplugin;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;

import com.sap.mw.jco.IMetaData;
import com.sap.mw.jco.JCO;

public class Table {
    
    public static final String IMPORT = "import";
    public static final String EXPORT = "export";    
    
    /** Name of table */
    public String   Name;
    
    /** Where to put export table. If table is fetched as CompositeMap, this is a 'map path', such as
     *  '/model/result/list'; if table is fetched as Array, this is a 'attribute path', such as 
     *  '/model/result/@field'
     */ 
    public String   Target;
    
    /**
     * Type of table, 'export' or 'import'
     */
    public String   Type = EXPORT;
    
    /**
     * Source field path for import table
     */
    public String   Source_field;
    
    /**
     * For export table, how data is fetched.
     * 'map': the result is fetched as CompositeMap
     * 'array': the result is fetched as java.sql.Array
     */
    public String   Fetch_type = "map";
    
    /**
     * For fetch_type 'array': Name of PL/SQL collection type
     */
    public String   Collection_type;
    
    /**
     * For fetch_type 'array': Name of PL/SQL struct type for the array element
     */
    public String   Struct_type;
    
    Logger              logger;    
    
    boolean Dump = false;
    
    FieldMapping[]  field_mappings;
    HashMap         source_map;
    HashMap         name_map;
    
    public Table( Logger logger ){
        this.logger = logger;
    }
    
    public CompositeMap toCompositeMap(){
        CompositeMap table = new CompositeMap("jco","org.lwap.sapplugin","table");
        table.put("name", Name);
        table.put("type", Type);
        table.put("target", Target);
        table.put("source_field", Source_field);
        table.put("fetch_type", Fetch_type);
        table.put("collection_type", Collection_type);
        table.put("struct_type", Struct_type);
        if(field_mappings!=null){
            CompositeMap mappings = table.createChild("field-mappings");
            for(int i=0; i<field_mappings.length; i++){
                CompositeMap mapping = new CompositeMap("field-mapping");
                mapping.put("name", field_mappings[i].Name);
                mapping.put("source_name", field_mappings[i].Source_name);
                mappings.addChild(mapping);
            }
        }
        return table;
    }
    
    public void setFieldMappings(FieldMapping[] m){
        field_mappings = m;
        source_map = new HashMap();
        name_map = new HashMap();
        for(int i=0; i<m.length; i++){
            if(m[i].Name==null) throw new ConfigurationError("Must set 'name' for <field-mapping>");
            if(m[i].Source_name==null) throw new ConfigurationError("Must set 'source_name' for <field-mapping>");
            name_map.put(m[i].Name.toLowerCase(), m[i]);
            source_map.put(m[i].Source_name.toLowerCase(), m[i]);
        }
    }
    
    public FieldMapping[] getFieldMappings(){
        return field_mappings;
    }
    
    public boolean isImport(){
        return IMPORT.equalsIgnoreCase(Type);
    }
    
    public boolean isFetchTypeMap(){
        return "map".equalsIgnoreCase(Fetch_type);
    }
    
    public boolean isFetchTypeArray(){
        return "array".equalsIgnoreCase(Fetch_type);
    }
    
    public JCO.Table getJCOTable(JCO.ParameterList list){
        try{
            JCO.Table table = list.getTable(Name);
            if(table==null) 
                throw new IllegalArgumentException("Table '"+Name+"' doesn't exist");
            return table;
        }catch(Throwable t){
            throw new IllegalArgumentException("Can't get table '"+Name+"':"+t.getMessage());
        }
    }
    
    /**
     * Fill a JCO.Table with data from an Oracle PL/SQL Array
     * @param table
     * @param array
     * @throws SQLException
     */
    public void fillJCOTable(JCO.Table table,  Array array) throws SQLException {
        //IMetaData tmd = table.getMetaData();
        
        if(!(array instanceof ARRAY)){
            throw new IllegalArgumentException("Currently only oracle table is surpported");
        }
        Object[] items = (Object[])array.getArray();
        if(items==null || !(items.length>0)){
            if(Dump){
                logger.info("No rows fetched in PL/SQL table");
            }            
            return;
        }
        table.appendRows(items.length);
        //table.nextRow();
        ResultSetMetaData smd = null;
        if(Dump){
            logger.info("Appending " + items.length +" rows to ABAP table " + Name);
        }
        for(int i=0; i<items.length; i++){
            STRUCT s = (STRUCT)items[i];
            if(smd==null)
                smd = s.getDescriptor().getMetaData();
            Object[] values = s.getAttributes();
            for(int n=0; n<values.length; n++){
                String source_name = smd.getColumnName(n+1);
                String field_name = source_name;
                FieldMapping mapping = (FieldMapping)source_map.get(source_name.toLowerCase());
                if(mapping!=null) field_name = mapping.Name;
                table.setValue(values[n], field_name);
                if(Dump){
                    logger.info(field_name+" -> "+values[n]);
                }
            }
            table.nextRow();
            logger.info("================ end line "+i+"=====================");            
        }
        if(Dump){
            logger.info("\r\nTable transfered");
        }
    }
    
    /**
     * Fill a CompositeMap with records fetched from JCO.Table
     * @param records An instance of JCO.Table containing data
     * @param result Target CompositeMap to be filled with, each record in JCO.Table will be
     * created as a child record of CompositeMap
     * @return filled CompositeMap
     */
    public CompositeMap fillCompositeMap(JCO.Table records, CompositeMap result){
        records.firstRow();
        // loop return table
        for(int n=0; n<records.getNumRows(); n++, records.nextRow()){
            // create CompositeMap record
            CompositeMap item = new CompositeMap((int)(records.getFieldCount()*1.5));
            item.setName("record");
            // put all fields in table into CompositeMap
            for(int col=0; col<records.getNumColumns(); col++){
                item.put(records.getField(col).getName(), records.getValue(col));
            }
            result.addChild(item);
        }
        return result;
    }
    
    public Array fillArray(JCO.Table records, Connection conn)
        throws SQLException
    {
        if(Collection_type==null) throw new ConfigurationError("Must set 'collection_type' to fetch table as pl/sql collection");
        if(Struct_type==null) throw new ConfigurationError("Must set 'Struct_type' to fetch table as pl/sql collection");
        // Get pl/sql type descriptor
        ArrayDescriptor adesc = ArrayDescriptor.createDescriptor(Collection_type, conn);
        StructDescriptor sdesc = StructDescriptor.createDescriptor(Struct_type, conn);        
        // construct a map of pl/sql struct field name -> its field id
        HashMap  struct_map = new HashMap();        
        ResultSetMetaData md = sdesc.getMetaData();
        int field_count = md.getColumnCount();
        for(int i=1; i<=field_count; i++)
            struct_map.put(md.getColumnName(i).toLowerCase(), new Integer(i-1));
        // Array of pl/sql struct field id indexed by ABAP table field id
        // ids[sap_field_id] = Integer<pl/sql_field_id>        
        IMetaData smd = records.getMetaData();
        Integer[] ids = new Integer[smd.getFieldCount()];
        for(int i=0; i<smd.getFieldCount(); i++){
            String fname = smd.getName(i);
            FieldMapping mapping = (FieldMapping)name_map.get(fname.toLowerCase());
            if(mapping!=null)
                fname = mapping.Source_name;
            Integer id = (Integer)struct_map.get(fname.toLowerCase());
            ids[i] = id;
        }
       
        // Create Object array to hold  each record in ABAP table
        records.firstRow();
        Object[] elements = new Object[records.getNumRows()];
        for(int n=0; n<elements.length; n++, records.nextRow()){
            // Create Object array to hold all attributes in one record
            Object[] attribs = new Object[field_count];
            for(int c=0; c<ids.length; c++){
                if(ids[c]==null) continue;
                int id = ids[c].intValue();
                attribs[id] = records.getValue(c);
            }
            STRUCT rec = new STRUCT(sdesc, conn, attribs);
            elements[n] = rec;
        }
        ARRAY result =  new ARRAY(adesc, conn,elements );
        
        return result;
    }

}
