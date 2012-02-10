/**
 * Created on: 2002-11-17 19:56:16
 * Author:     zhoufan
 */
package org.lwap.validation;

import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.lwap.database.datatype.DataTypeManager;
import org.lwap.database.datatype.DatabaseTypeField;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class RequestParameterSource implements ParameterSource {
	
	HttpServletRequest request;
	String			   encoding = "utf-8";
	CompositeMap	   default_param;
	
	
	public RequestParameterSource( HttpServletRequest request){
		this.request = request;
	}
	
	public RequestParameterSource( HttpServletRequest request, CompositeMap param){
		
		this.request = request;
		this.default_param = param;
	}	
    
    String toString( Object obj){

        if( obj == null) return "";
        
        DatabaseTypeField f = null;
        f=DataTypeManager.getType(obj);
        if( f==null) return obj.toString();
        else{
            //System.out.println("formating "+f);
            String s=f.format(obj);
            return s;
        }

      }
	
	public String getParameter( String key){

		if( default_param != null){
			Object value = default_param.get(key);
			if( value != null)
				return  toString(value);
		}
        
		String str = request.getParameter(key);
		if( str != null) 
			if( str.length()==0) return null;		
		return str;
/*		
		if( str != null){ 
			if( str.length()==0) return null;
			try{
				return new String( str.getBytes("ISO8859_1"), encoding);
			}catch(Exception ex){
				return str;
			}
		}
		
		else return null;
*/
		}
	
	public Collection getParameters( String key){
		String[] values = request.getParameterValues(key);
		if( values == null) 
			return null;
		LinkedList params = new LinkedList();
		for( int i=0; i<values.length; i++)
			params.add(values[i]);
		return params;
		
	}
	
	public String[] getParameterArray( String key){
		if( default_param != null){
			Object value = default_param.get(key);
			if( value != null){
				String[] rt = new String[1];
				rt[0] = value.toString();
				return rt;
			}
		}
		return request.getParameterValues(key);	
	}

}
