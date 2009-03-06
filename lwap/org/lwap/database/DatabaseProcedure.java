/**
 * Created on: 2004-3-1 13:22:40
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;

/**
 * 
 */
public class DatabaseProcedure extends DatabaseAccess {
	
/*
   		<!-- 
			Name: name of procedure to call
			ReturnTarget: root element name to put return value and out params
			ReturnField: attribute name to put
		-->
   		<procedure Name="test_pkg.proc_test" ReturnTarget="result" ReturnField="@RESULT" DataType="java.lang.Integer" >
			<!--
				if parameter needs input, either specify 'InputField' to get input value from context, or directly specify 'Value' and 'DataType'
				if parameter needs output, specify 'DataType' and 'ReturnField'
			-->
			<param InputField="/model/INPUT/@INPUT_NUM" />
			<param DataType="java.sql.Date" ReturnField="@RETURN_DATE"  /> 
			<param DataType="java.lang.String" ReturnField="INPUT/@STR" DBTypeName="Oracle type name"  Value="test string" />  
		</procedure>
*/	
	
	public static final String KEY_NAME 			= "Name";
	public static final String KEY_DATA_TYPE 	= "DataType";
	public static final String KEY_INPUT_FIELD 	= "InputField";				
	public static final String KEY_RETURN_TARGET  = "ReturnTarget";
	public static final String KEY_RETURN_FIELD 	= "ReturnField";
	public static final String KEY_VALUE 		= "Value";
    public static final String KEY_DB_TYPE_NAME = "DBTypeName";
	
	public class Parameter {
		
		public String 					return_field;
		public Object 					data_value;
		public DatabaseTypeField 		dt;
        public String                   db_type_name;
		
		public Parameter( int data_type){
			this.dt = DataTypeManager.getType(data_type);
			if( dt == null) throw new IllegalArgumentException("unknown data type for JDBC code " + data_type);
		}
		
		public Parameter( String data_type){
			dt = DataTypeManager.getType(data_type);
//			System.out.println("constr:"+data_type+" "+dt);
			if( dt == null) throw new IllegalArgumentException("unknown data type for " + data_type);
		}
		
		public Parameter( Object data_value){
			this.data_value = data_value;
			if( data_value != null) dt = DataTypeManager.getType(data_value);
		}
		
		public String toString(){
			return "<Parameter DataType='" + dt +"' ReturnField='" + return_field + "' DataValue='" + data_value +"' />";
		}
        
        public void registerOutParameter( CallableStatement stmt, int id)
            throws SQLException
        {
            if(db_type_name==null)
                dt.registerParam(stmt, id);
            else{
                stmt.registerOutParameter(id, dt.getSQLType(), db_type_name);
            }                
        }
		
	};

	/**
	 * @see org.lwap.database.DatabaseAccess#getAccessType()
	 */
	public int getAccessType() {
		return DatabaseAccess.ACCESS_TYPE_STORED_PROCEDURE;
	}
    
	/**
	 * @see org.lwap.database.DatabaseAccess#execute(Connection, CompositeMap, CompositeMap)
	 */
	public void execute(
		Connection conn,
		CompositeMap parameter,
		CompositeMap target)
		throws SQLException {
		
		StringBuffer sql_stmt = new StringBuffer();
		int 		 param_index = 1;
		ArrayList	 params = null;
		if( getObjectContext().getChilds() != null)
			params = new ArrayList(getObjectContext().getChilds().size());
		else
			params = new ArrayList(1);
			
		// get name of procedure to call		
		String	 name = getString(KEY_NAME);
		if( name == null) throw new IllegalArgumentException("DatabaseProcedure: must specify 'Name' for procedure to call");
		name = TextParser.parse(name, parameter);
		
		// get target CompositeMap to hold return values
		CompositeMap target_context = null;
		String return_target = getString(KEY_RETURN_TARGET);
		if( return_target != null){
			target_context = target.createChildByTag(return_target);
		} else
			target_context = target;
		
		// judge whether the procedure to call has return value
		String return_field = getString(KEY_RETURN_FIELD);
		String data_type = null;
		if( return_field != null){
			data_type = getString(KEY_DATA_TYPE);
			if( data_type == null) throw new IllegalArgumentException("DatabaseProcedure: must specify 'DataType' if 'ReturnField' is set");
			sql_stmt.append("{ ? = call ").append(name);
			// create return parameter
			Parameter return_value = new Parameter(data_type);
			return_value.return_field = return_field;
            return_value.db_type_name = getString(KEY_DB_TYPE_NAME);
			params.add(return_value);
		} else{
			sql_stmt.append("{ call ").append(name);
		}
		
		Iterator it = getObjectContext().getChildIterator();
		int param_count = 0;
		if( it != null){
			while( it.hasNext()){
				CompositeMap param_config = (CompositeMap) it.next();
				if( param_count == 0) sql_stmt.append('(');
				else sql_stmt.append(',');
				
				String input_fld  = param_config.getString(KEY_INPUT_FIELD);
				String param_type = param_config.getString(KEY_DATA_TYPE);
				String data_value = param_config.getString(KEY_VALUE);
				Parameter param = null;
				
				//judge if parameter need input
				if( input_fld != null ){
					Object obj = parameter.getObject(input_fld);
					if( obj == null){
						if( param_type == null) throw new IllegalArgumentException("DatabaseProcedure: Must specify 'DataType' for parameter " + param_config.toXML() +" if 'InputField' is not set");
						param = new Parameter(param_type);	
					}
					else{
						param = new Parameter(obj);
					}
				} else{ 
					if( param_type == null) throw new IllegalArgumentException("DatabaseProcedure: Must specify 'DataType' for parameter " + param_config.toXML() +" if 'InputField' is not set");
					param = new Parameter(param_type);
					if(data_value != null) 
					try
					{
						param.data_value =  param.dt.parseObject(data_value);
					}catch(java.text.ParseException ex){
						throw new IllegalArgumentException("DatabaseProcedure: 'Value' format invalid for parameter " + (param_count)+" " + ex.getMessage()); 
					}
					
				}
				param.return_field = param_config.getString(KEY_RETURN_FIELD);
                param.db_type_name = param_config.getString(KEY_DB_TYPE_NAME);                
				params.add(param);
				sql_stmt.append('?');
				param_count++;
			}
		}	
		if( param_count>0) sql_stmt.append(')');
		sql_stmt.append('}');
		
        String sql = sql_stmt.toString();        
        /* for debug */
        if( "true".equals(getString("Dump"))){
            System.out.println("DatabaseProdecure:"+sql);
            if( parameter == null)
                System.out.println("Parameter is null");
            else
                System.out.println("Parameter:" + parameter.toXML());   
        } 
        long execTime = System.currentTimeMillis();        
		CallableStatement stmt = conn.prepareCall(sql);	
		
		try{
			// set input value
			for( int i=0; i<params.size(); i++){
				Parameter pm = (Parameter)params.get(i);
				if( pm.data_value != null)	{
                    if(pm.dt==null) throw new ConfigurationError("Can't get DataType for parameter "+pm.toString());
					pm.dt.setFieldObject(pm.data_value,stmt,i+1);
				}else{
					if( pm.return_field == null){ 
//						System.out.println("set null:"+pm);
						stmt.setNull(i+1, pm.dt.getSQLType());
					}
				}
                // define output parameter
				if( pm.return_field != null){
                    pm.registerOutParameter(stmt, i+1);
                }
			}
			stmt.execute();

			// get output value and put into target CompositeMap
			for( int i=0; i<params.size(); i++){
				Parameter pm = (Parameter)params.get(i);
				if( pm.return_field != null){
					if( pm.dt == null) throw new SQLException("DatabaseProcedure: can't decide DataType for output parameter No." + (i+1)+", maybe InputField is null");
					Object obj = pm.dt.getObject(stmt,i+1);
					target_context.putObject(pm.return_field,obj,true);
				}
			}
            
            // record execution time
            super.recordTime(sql, System.currentTimeMillis()-execTime);
		} finally{
			if( stmt != null) stmt.close();
		}				
		
	}

}
