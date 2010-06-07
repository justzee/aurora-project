/*
 * Created on 2007-6-14
 */
package org.lwap.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwap.controller.ControllerProcedures;
import org.lwap.controller.IController;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import uncertain.proc.ProcedureRunner;

/** Parse input stream in JSON format and put into parameter */

public class JsonInput implements IController{
    
    public static final String KEY_REQUEST_DATA = "_request_data";
    
    public String   Parameter;
    
    MainService     service;
    Logger          logger;
    
    public JsonInput(Logger logger){
        this.logger = logger;
    }
    
    public void onParseParameter(ProcedureRunner runner)
        throws IOException, JSONException
    {
        Object obj = null;
        
        CompositeMap context = runner.getContext();
        HttpServletRequest  request = service.getRequest();
        // temporary check _request_data
        String str = request.getParameter(KEY_REQUEST_DATA);
        if(str!=null)
            if(str.length()>0)
                Parameter = KEY_REQUEST_DATA;
        // end
        if(Parameter==null){
            InputStream is = request.getInputStream();
            obj = JSONAdaptor.createJSONObject(is);
        }else{
            String input = request.getParameter(Parameter);
            if(input!=null) 
                if(input.length()==0) input = null;
            if(input==null){
                logger.warning("[JsonInput] specified parameter '"+Parameter+"' is not passed from request");
                return;
            }
            try{
            	if(input.startsWith("{")) {
            		obj = new JSONObject(input);
            	} else {
            		obj = new JSONArray(input);
            	}
            } catch(JSONException ex){
                logger.warning("[JsonInput] error when parsing json input"+ex.getMessage());
                return;
            }
        }
        
        CompositeMap params = service.getParameters();
        if(obj instanceof JSONObject) {
        	CompositeMap map = JSONAdaptor.toMap((JSONObject)obj, Parameter==null?"object":Parameter);
        	params.addChild(map);        
        }else {
        	JSONArray array = (JSONArray)obj;
        	int length = array.length();
        	for(int i = 0; i <length; i++) {
        		JSONObject jobj = (JSONObject)array.get(i);
        		CompositeMap map = JSONAdaptor.toMap(jobj, Parameter==null?"object":Parameter);
            	params.addChild(map);
        	}
        }
    }
    
    public void setServiceInstance(MainService service_inst){
        service = service_inst;    
    }
    
    public int detectAction( HttpServletRequest request, CompositeMap context ){
        return IController.ACTION_DETECTED;
    }

    public String getProcedureName(){
        return "org.lwap.controller.InvokeService";
        //return ControllerProcedures.PARAMETER_INPUT;
    }
}
