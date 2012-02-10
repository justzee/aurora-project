/**
 * Created on: 2002-11-14 17:27:17
 * Author:     zhoufan
 */
package org.lwap.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

public class ParameterParser {

	public static final String GROUP = "group";
	public static final String KEY_ELEMENT_NAME = "ElementName";

/*			
	public static void parseParameterGroup( ParameterSource source, CompositeMap param_config, CompositeMap parameter){
		try{
			String elm_name = param_config.getString("ElementName","group");
			InputParameterImpl param =  (InputParameterImpl)DynamicObject.cast(param_config, InputParameterImpl.class);
			String param_name = param.getParameterName();
			String[] values = source.getParameterArray(param_name);
			if( values == null) return;
			for(int i=0; i<values.length; i++){
				Object value 
			}
		} catch(Throwable thr){
		}
		
	}
*/	
	


	/**
	 * 
	 * <parameter Name="param_name" [DataType="java.lang.String" ] Nullable="true|false" DefaultValue="default_value" />
	 * 
	 * parse parameter defined in a InputParameterImpl collection,
	 * and save parsed parameter in Map
	 * @param request HttpServletRequest instance
	 * @param parameter_define a collection of Map that can cast to InputParameterImpl
	 * @return Collection of ValidationException on parse process. If no exception thrown, 
	 * the return value is null.
	 * @throws IllegalArgumentException if there is configuration error in parameter_define
	 */
	public static Collection parseParameter( HttpServletRequest request, Collection parameter_define, CompositeMap parameter  ) 
	{
		return parseParameter( new RequestParameterSource(request), parameter_define, parameter);
	}
	
	static void parseParameterList(  ParameterSource source, Collection parameter_define, CompositeMap parameter, Collection error_list, String posit_fix){
		
	}
	
	public static Collection parseParameter( ParameterSource source, Collection parameter_define, CompositeMap parameter ){
		if( parameter_define == null) return null;
		Collection  parse_error_list = null;
		Iterator it = parameter_define.iterator();
		while( it.hasNext()){
			try{
				CompositeMap param_map = (CompositeMap)it.next();
			    //System.out.println("Param "+param_map.getString("Name"));
				InputParameterImpl param = null;
				try{
					param = (InputParameterImpl) DynamicObject.cast(param_map,InputParameterImpl.class);
				} catch(Throwable thr){
					System.err.println("Can't create instance of "+InputParameterImpl.class+": "+thr.getMessage());
				}
				
				if( GROUP.equals(param_map.getName()) ){				    
					String param_name = param.getParameterName();
					if(param_name == null) throw new IllegalArgumentException("<group> Must specify 'Name' property");
					//System.out.println("Parsing group "+param_name);
					CompositeMap group_map = parameter.createChild(param_map.getString("Target","group"));
					String element_name = param_map.getString("ElementName", "param" );
					
					String[] values = source.getParameterArray(param_name);
					if( values == null) {
					    System.out.println("<group> Warning: parameter "+param_name+" from request is null");
					    if(param_name.indexOf(' ')>=0){
					        System.out.println("<group> Warning: parameter "+param_name+" contains white space");
					    }
					    continue;
					}
					
					for( int i=0; i<values.length; i++){
						String value = values[i];
						if( value.length()==0) continue;
						CompositeMap group_item = group_map.createChild(element_name);
						//parseSingleParameter(param, param.getParameterName(), source, group_item);
						try{
							group_item.put(param.getParameterName(), param.parseString(value));
						} catch(ValidationException e){
						    if( parse_error_list == null) parse_error_list = new LinkedList();
							parse_error_list.add(e);
							continue;
						}
						Iterator item_it = param_map.getChildIterator();
						if( item_it == null) continue;
						while( item_it.hasNext()){
							CompositeMap g_param_map = (CompositeMap)item_it.next();
							InputParameterImpl g_param_impl = (InputParameterImpl)InputParameterImpl.createInputParameter(g_param_map);
							try{
							    parseSingleParameter( g_param_impl, g_param_impl.getParameterName()+value, g_param_impl.getParameterName(), source, group_item);
							}catch(ValidationException ex){
								if( parse_error_list == null) parse_error_list = new LinkedList();
								parse_error_list.add(ex);
							}							
						}
					}
						
					//System.out.println(group_map.toXML());
				} else {
					parseSingleParameter( param, param.getParameterName(), source, parameter);
				}
			}			  
			catch(ValidationException ex){
				if( parse_error_list == null) parse_error_list = new LinkedList();
				parse_error_list.add(ex);
			}
		}
		return parse_error_list;		
	}
	
	static void parseSingleParameter( InputParameterImpl param, String param_name, ParameterSource source, CompositeMap parameters ) throws ValidationException{
		Object value = param.parseString(source.getParameter(param_name));
		if( value != null)
			parameters.put( param_name, value);

	}
	
	static void parseSingleParameter( InputParameterImpl param, String param_name, String target_param_name, ParameterSource source, CompositeMap parameters ) throws ValidationException{
		Object value = param.parseString(source.getParameter(param_name));
		if( value != null)
			parameters.put( target_param_name, value);

	}
/*	
	public static Object parseParameter( HttpServletRequest request, InputParameter param){
		try{	
			return param.parseString( request.getParameter( param.getParameterName()) );
		} catch(Exception ex){
			return null;
		}
		
	}
	*/
}
