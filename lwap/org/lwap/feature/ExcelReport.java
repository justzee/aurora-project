/*
 * Created on 2005-12-9
 */
package org.lwap.feature;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.lwap.application.NoPrivilegeException;
import org.lwap.controller.MainService;
import org.lwap.database.oracle.BlobUtil;
import org.lwap.mvc.excel.ExcelDataTable;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

/**
 * ExcelReport
 * @author Zhou Fan
 * 
 */
public class ExcelReport  implements IFeature{

	public static final String KEY_IS_GENERATE_SCRIPT = "IS_GENERATE_SCRIPT";	

	public static final String KEY_REPORT_NAME = "REPORT_NAME";
	public static final String KEY_SESSION_ID_STRING = "SESSIONIDSTR";
	public static final String KEY_VIEW_ID = "ViewID";
	public static final String KEY_SESSION_ID = "session_id";
	public static final String KEY_URL_PARAM = "URL_PARAM";
	
	Logger			logger;
	
    Object			session_id;
    String  		session_id_str;
    MainService 	service;
    CompositeMap	context;
    CompositeMap	report_config;
    
	String 		view_id;
	boolean 	is_generate_script = true; 
    // whether this report is called from external sources other than web application
    boolean     is_called_external = false;
    
    public void postParseParameter() throws Exception {
        
        if(!is_called_external) return;
        
        CompositeMap param = service.getParameters();
        HttpServletRequest request = service.getRequest();
        
        
        String sid = request.getParameter(KEY_SESSION_ID_STRING);
        if( sid != null) param.put(KEY_SESSION_ID_STRING, sid);
        param.put(KEY_REPORT_NAME,  service.getServiceName());
        
        
        view_id  = request.getParameter(KEY_VIEW_ID);
        is_generate_script = view_id == null;
        
        CompositeMap    param_def = new CompositeMap();
        service.databaseAccess("ExcelReportParamQuery.data", param, param_def);
        
        CompositeMap tmpMap = param_def.getChild("SID");
        session_id =  tmpMap.get("SESSION_ID");
        if( tmpMap != null) param.put(KEY_SESSION_ID, session_id);
        
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
	
    // get session_id & session_id_str
    public void onBeginService(ProcedureRunner runner) 
    throws Exception {
		context = runner.getContext();
		service = MainService.getServiceInstance(context);
        session_id = context.getObject("/session/@session_id");
        session_id_str = service.getRequest().getParameter(KEY_SESSION_ID_STRING);
		CompositeMap params = service.getParameters(), model = service.getModel();
        is_generate_script = !ExcelDataTable.isGenerateData(service.getRequest());        
        		
		if(session_id==null){		     
		    if(session_id_str==null) throw new NoPrivilegeException();
            if(is_generate_script){
                // requested from other source, parameters saved in database
                is_called_external = true;
            }else{
                // requested from excel, get session_id from request parameter
    		    params = service.getParameters();
    		    params.put(KEY_SESSION_ID_STRING, session_id_str);
    		    service.databaseAccess("GetSessionIDFromESID.data", params, model);
    		    session_id = model.getObject("EXCEL-REPORT-SESSION/@SESSION_ID");
                if(session_id==null) throw new NoPrivilegeException();
            }
            
		}
        /*
        else if(){
            
        }
        */
		//  requested from browser, get session_id from session
		else{		    
		    service.databaseAccess("PrepareExcelQuery.data", params, model );
		    Object o = model.getObject("EXCEL-REPORT-SESSION/@ENCODED_SESSION_ID");
		    if(o==null) throw new IllegalStateException("Can't get encoded session id");
		    session_id_str = (String)o;	
		}

		service.getRequest().setAttribute(KEY_IS_GENERATE_SCRIPT, new Boolean(is_generate_script) );
    }
    
	public int onParseParameter(ProcedureRunner runner){
	    // don't parse parameter if in generate data mode
	    if(!is_generate_script){
	        return EventModel.HANDLE_STOP;
	    }
        return EventModel.HANDLE_NORMAL;
	}    
    
    public int attachTo(CompositeMap config, Configuration procConfig) {
        report_config = config;
        return IFeature.NORMAL;
    }	
	
	public static boolean isGenerateScript( HttpServletRequest request){
		Boolean b = (Boolean) request.getAttribute(KEY_IS_GENERATE_SCRIPT);
		if( b == null) return true;
		return b.booleanValue();
	}

	public int onCreateModel(ProcedureRunner runner) throws Exception {
        int result = EventModel.HANDLE_NO_SAME_SEQUENCE;
	    Connection conn = null;
		HttpServletRequest request = service.getRequest();
		String method = request.getMethod();
		try{
			if( is_generate_script){
				service.createModel();
				CompositeMap model = service.getModel();
				if( model == null) {
				    logger.warning("[CreateModelFromQuery] model is null, no data generated");
				    return result;
				}
				CompositeMap parent = model.getParent();
				model.setParent(null);
				conn = service.getConnection();
				BlobUtil.saveObject(conn,"excel_report_session","report_data","session_id=" + session_id,model);
				conn.commit();
				model.setParent(parent);
				
			} else{

					conn = service.getConnection();
					CompositeMap model = 
					(CompositeMap)BlobUtil.loadObject(conn,"select report_data from excel_report_session s where s.session_id = " + session_id);
					if( model != null){
					    /*
					    if(model.getChilds().size()==0){
					        logger.warning("[LoadModelFromDB] model loaded from DB is of size 0");
					    }
					    */
						CompositeMap context = service.getServiceContext();
						CompositeMap m = service.getModel();
						if(m==null)
						    context.addChild( model);
						else
						    context.replaceChild(m,model);
/*
						logger.info("[LoadModelFromDB]");
						logger.info(model.toXML());
*/
					}else{
					    logger.warning("[LoadModelFromDB] model loaded from DB is null");					    
					}

			}
		} finally {
			if( conn != null) try{
				conn.close();
			} catch(SQLException ex){
			}
		}
        return result;
	}

	public void preCreateView(){
	    service.getServiceConfig().put("content-type",getContentType());
	    service.getRequest().setAttribute(KEY_SESSION_ID_STRING, session_id_str);
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

    public ExcelReport(Logger logger) {
        this.logger = logger;
    }

}
