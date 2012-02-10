package org.lwap.feature;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.lwap.controller.IController;
import org.lwap.controller.MainService;

import aurora.service.ServiceContext;
import aurora.service.json.JSONServiceContext;

import uncertain.composite.CompositeMap;
import uncertain.composite.JSONAdaptor;
import uncertain.event.EventModel;
import uncertain.proc.ProcedureRunner;

public class JsonParameterParse implements IController{
	public static final String HEAD_JSON_PARAMETER = "json-parameter";
	public static final String DEFAULT_JSON_PARAMETER = "_request_data";
	MainService service;
	Logger logger;
	public JsonParameterParse(Logger logger){
		this.logger = logger;
	}
	public int preParseParameter(JSONServiceContext ct) throws IOException, JSONException{
		ServiceContext context = ct;
        HttpServletRequest  request = service.getRequest();
        request.setCharacterEncoding("utf-8");
        String jparam = request.getHeader(HEAD_JSON_PARAMETER);
        if (jparam == null)
            jparam = DEFAULT_JSON_PARAMETER;

        String content = request.getParameter(jparam);
        if (content != null) {
            JSONObject jobj = new JSONObject(content);
            CompositeMap root = JSONAdaptor.toMap(jobj);
            if (root == null)
                return EventModel.HANDLE_STOP;
            CompositeMap param = root.getChild("parameter");
            if (param != null)
            	context.setParameter(param);
            return EventModel.HANDLE_STOP;
        } else
            return EventModel.HANDLE_NORMAL;
	}
	public int detectAction(HttpServletRequest request, CompositeMap context) {
		return IController.ACTION_NOT_DETECTED;
	}

	public String getProcedureName() {		
		return "org.lwap.controller.BaseService";
	}

	public void setServiceInstance(MainService serviceInst) {
		service = serviceInst; 		
	}

}
