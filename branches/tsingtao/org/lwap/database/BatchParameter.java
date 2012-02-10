/**
 * Created on: 2003-3-4 11:23:46
 * Author:     zhoufan
 */
package org.lwap.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import org.lwap.validation.InputParameter;
import org.lwap.validation.InputParameterImpl;
import org.lwap.validation.ParameterSource;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 *  <batch-parameter ParameterSource="access path to get model for batch parameters" 
 *                   Target ="access path to put parsed parameter, tag string supported" 
 *                   Name="FIELD${@FIELD_ID}" DataType="java.lang.String" Nullable="true" 
 * 					 ParseFromRequest="true|false"
 * 					 />
 */
public class BatchParameter extends DatabaseAccess {
	
	public static final String KEY_PARAMETER_SOURCE = "ParameterSource";
	public static final String KEY_PARSE_FROM_REQUEST = "ParseFromRequest";

	/**
	 * @see org.lwap.database.DatabaseAccess#getAccessType()
	 */
	public int getAccessType() {
		return 0;
	}

	/**
	 * @see org.lwap.database.DatabaseAccess#execute(Connection, CompositeMap, CompositeMap)
	 */
	public void execute(
		Connection conn,
		CompositeMap parameter,
		CompositeMap target)
		throws SQLException {
			
		CompositeMap	config = getObjectContext(); 	
		String 			param_name   = config.getString("Name");
		String			param_source = config.getString(KEY_PARAMETER_SOURCE);
		boolean		parse_from_request = config.getBoolean(KEY_PARSE_FROM_REQUEST, true);
		String          target_path  = getTarget();
		
		CompositeMap    context		 = getObjectContext();
		
		if( getService() == null) return;
		ParameterSource src = getService().getParameterSource();
		
		CompositeMap    model = (CompositeMap)parameter.getObject(param_source);		
		if( model == null) return;
		
		Iterator it = model.getChildIterator();
		if( it == null) {
			System.out.println("BatchParameter: No items found in specified model");
			System.out.println(model.toXML());
			return;
		}
		while( it.hasNext()){
			CompositeMap item  = (CompositeMap)it.next();
			String name 	   = TextParser.parse(param_name,item);
			String target_name = TextParser.parse(target_path,item);
			if( parse_from_request){
				context.put("Name", name);
				InputParameter param = InputParameterImpl.createInputParameter(context);
				try{
					Object vl = param.parseString( src.getParameter(name));
					//System.out.println(target_name+"="+vl);
					if( vl != null) item.put( target_name, vl);
				} catch(Exception ex){
					throw new SQLException(ex.getMessage());
				}
			}else{
				item.put( target_name, parameter.get(name));
			}		
		}
		
	}

}
