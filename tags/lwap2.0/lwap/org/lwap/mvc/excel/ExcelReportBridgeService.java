/**
 * Created on: 2004-9-1 15:40:57
 * Author:     zhoufan
 */
package org.lwap.mvc.excel;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;
import org.lwap.application.ServiceDispatch;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 * 
 */
public class ExcelReportBridgeService extends BaseService {
	
	public static final String KEY_TARGET = "target";
	
	
	CompositeMap params = new CompositeMap(10);
	
	public void addParameter(String param_name, Object value) throws ServletException {
		String file_name;
		params.put("name", param_name);
		params.put("value", value);
		if( value instanceof Number)
			file_name = "AddNumberParam.data";
		else if( value instanceof java.util.Date )
			file_name = "AddDateParam.data";
		else
			file_name = "AddVarcharParam.data";	
		databaseAccess(file_name,params,params);
	}
	
	

	/**
	 * @see org.lwap.application.BaseService#doService()
	 */
	public void doService() throws IOException, ServletException {
		CompositeMap context = getServiceContext();
		databaseAccess("PrepareExcelQuery.data", context, context);
		params.put("session_id", context.getObject("/session/@session_id"));
		// add all parameters
		CompositeMap parameters = this.getParameters();
		Iterator it = parameters.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			addParameter(entry.getKey().toString(), entry.getValue());
		}
		super.doService();
	}
	
	/**
	 * 
	 */
	public ServiceDispatch getServiceDispatch(){
			String encoded_session_id;
			try{
				this.databaseAccess("GetSessionID.data", params, params);
			}catch(Exception ex){
				application.getLogger().warning(ex.getMessage());
				return null;
			}
			encoded_session_id = params.getString("SID");
			if(encoded_session_id == null){
				application.getLogger().warning("Can't get encoded session_id");
				return null;
			}
			String target = getServiceConfig().getString(KEY_TARGET);
			if(target==null) throw new IllegalArgumentException("'target' attribute must be set in service config");
			target = TextParser.parse(target, this.getServiceContext());
			target = target + '?' + ExcelExportService.KEY_SESSION_ID_STRING + '=' + encoded_session_id;
			ServiceDispatch disp = application.createDispatch(
				this,
				ServiceDispatch.TARGET_TYPE_URL,
				target,
				ServiceDispatch.DISPATCH_STYLE_REDIRECT
			);
			return disp;
	}

}
