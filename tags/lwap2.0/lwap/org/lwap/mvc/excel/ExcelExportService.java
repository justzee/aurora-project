/**
 * Created on: 2003-9-8 14:10:13
 * Author:     zhoufan
 */
package org.lwap.mvc.excel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.lwap.application.BaseService;
import org.lwap.database.oracle.BlobUtil;
import org.lwap.mvc.BuildSession;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class ExcelExportService extends BaseService {

	public static final String KEY_IS_GENERATE_SCRIPT = "IS_GENERATE_SCRIPT";	

	public static final String KEY_REPORT_NAME = "REPORT_NAME";
	public static final String KEY_SESSION_ID_STRING = "SESSIONIDSTR";
	public static final String KEY_VIEW_ID = "ViewID";
	public static final String KEY_SESSION_ID = "session_id";
	public static final String KEY_URL_PARAM = "URL_PARAM";
	
	public static void setUrlParameter( BuildSession session, String url_param ) {
		String param = (String)session.getProperty(KEY_URL_PARAM);
		if( param == null) param = url_param;
		else param = param + '&' + url_param;
		session.setProperty(KEY_URL_PARAM, param);
	}
	
	public static String getUrlParameter( BuildSession session){
		return (String) session.getProperty(KEY_URL_PARAM);
	}
	
	/**
	 *  get report session id from parameter, which is decoded by parseParameter
	 */
	public String getSessionID(){
		return getParameters().getString(KEY_SESSION_ID);
	}
	
		
	String 		view_id;
	boolean 	is_generate_script = true;
	
	public static boolean isGenerateScript( HttpServletRequest request){
		Boolean b = (Boolean) request.getAttribute(KEY_IS_GENERATE_SCRIPT);
		if( b == null) return true;
		return b.booleanValue();
	}

	public void createModel() throws IOException, ServletException {
		String session_id = getSessionID();
		Connection conn = null;
		HttpServletRequest request = this.getRequest();
		String method = request.getMethod();
		
		try{
			if( view_id == null && method.equalsIgnoreCase("GET")){
				super.createModel();
				CompositeMap model = this.getModel();
				if( model == null) return;
				CompositeMap parent = model.getParent();
				model.setParent(null);
				try{
					conn = this.getConnection();
					BlobUtil.saveObject(conn,"excel_report_session","report_data","session_id=" + session_id,model);
/*
					CompositeMap m = 
					(CompositeMap)BlobUtil.loadObject(conn,"select report_data from excel_report_session s where s.session_id = " + session_id);
*/					
					conn.commit();
				} catch(Exception ex){
					throw new ServletException( ex);
				}
				model.setParent(parent);
				
			} else{
				try{
					conn = this.getConnection();
					CompositeMap model = 
					(CompositeMap)BlobUtil.loadObject(conn,"select report_data from excel_report_session s where s.session_id = " + session_id);
					if( model != null){
						CompositeMap context = this.getServiceContext();
						context.addChild(model);
					}
				} catch(Exception ex){
					throw new ServletException( ex);
				}

			}
		} finally {
			if( conn != null) try{
				conn.close();
			} catch(SQLException ex){
			}
		}
		
		//System.out.println( "finished create model " + System.currentTimeMillis());
	}



	/**
	 * @see org.lwap.application.BaseService#parseParameter()
	 */
	public void parseParameter() throws IOException, ServletException {
		
		super.parseParameter();

		CompositeMap param = this.getParameters();
		HttpServletRequest request = getRequest();
		
		String sid = request.getParameter(KEY_SESSION_ID_STRING);
		if( sid != null) param.put(KEY_SESSION_ID_STRING, sid);
		param.put(KEY_REPORT_NAME,  getServiceName());
		
		
		view_id  = getRequest().getParameter(KEY_VIEW_ID);
		is_generate_script = view_id == null;
		getRequest().setAttribute(KEY_IS_GENERATE_SCRIPT, new Boolean(is_generate_script));
		
		CompositeMap	param_def = new CompositeMap();
		this.databaseAccess("ExcelReportParamQuery.data", param, param_def);
		
		CompositeMap tmpMap = param_def.getChild("SID");
		if( tmpMap != null) param.put(KEY_SESSION_ID, tmpMap.get("SESSION_ID"));
		
		tmpMap = param_def.getChild("param-list");
		if( tmpMap != null){
			Iterator it = tmpMap.getChildIterator();
			if( it == null) return;
			while( it.hasNext()){
				CompositeMap item = (CompositeMap)it.next();
				String name = item.getString("PARAM_NAME");
				String type = item.getString("DATA_TYPE");
				Object value = item.get(type +"_VALUE");
				param.put(name,value);
			}
		}
		

	}
	
	protected void createTemplate(){
		CompositeMap view = getViewConfig();
		CompositeMap svc_context = getServiceContext();
		svc_context.addChild(view);
	}
	

	/**
	 * @see org.lwap.application.BaseService#getContentType()
	 */
	public String getContentType() {
		if( is_generate_script)
			return "text/html;charset=gbk";
		else
			return "text/plain;charset=gbk";
	}

}
