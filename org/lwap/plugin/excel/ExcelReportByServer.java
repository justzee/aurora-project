package org.lwap.plugin.excel;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.AbstractController;
import org.lwap.controller.IController;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

public class ExcelReportByServer extends AbstractController implements IFeature {
	final static String KEY_EXCEL_FORMAT="format" ;
	final static String KEY_EXCEL_TEMPLATE_PATH="excel-template-path" ;
	final static String KEY_TEMP_PATH="temp-path" ;
	UncertainEngine mEngine;
	CompositeMap config;

	public ExcelReportByServer(UncertainEngine engine) {
		super(engine);
		mEngine = engine;
	}

	public int attachTo(CompositeMap config, Configuration procConfig) {
		this.config=CompositeUtil.attributeNameToLower(config);		
		return IFeature.NORMAL;
	}

	public int detectAction(HttpServletRequest request, CompositeMap context) {		
		return IController.ACTION_NOT_DETECTED;
	}

	public int preBuildOutputContent(ProcedureRunner runner) throws Exception {	
		CompositeMap appConfig=ServiceInstance.getApplicationConfig();		
		ExcelReport excelConfig=(ExcelReport)mEngine.getOcManager().createObject(config);			
		CompositeMap model = ServiceInstance.getModel();	
		CompositeMap parameterMap=ServiceInstance.getParameters();		
		excelConfig.setFileFormat(parameterMap.getString(KEY_EXCEL_FORMAT));
		HttpServletRequest request=ServiceInstance.getRequest();
		excelConfig.setTemplatePath(request.getRealPath(appConfig.getString(KEY_EXCEL_TEMPLATE_PATH)));
		excelConfig.setTempPath(request.getRealPath(appConfig.getString(KEY_TEMP_PATH)));
		File file=ExcelFactory.createExcel(model, excelConfig);
		HttpServletResponse response=ServiceInstance.getResponse();
		ExcelUtil.printOutExcel(file, response);
		file.delete();
		return EventModel.HANDLE_STOP;
	}
}
