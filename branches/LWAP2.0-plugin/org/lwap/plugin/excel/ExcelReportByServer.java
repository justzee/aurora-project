package org.lwap.plugin.excel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
	final static String KEY_FILE_NAME="_filename" ;
	public static String KEY_EXCEL_EXPORT_STATUS="_excel_export_status";
	
	UncertainEngine mEngine;
	CompositeMap config;
	ExcelReport excelConfig;
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
		CompositeMap appConfig=null;
		try{
			appConfig=ServiceInstance.getApplicationConfig();			
			excelConfig=(ExcelReport)mEngine.getOcManager().createObject(config);			
			CompositeMap model = ServiceInstance.getModel();	
			CompositeMap parameterMap=ServiceInstance.getParameters();		
			excelConfig.setFileFormat(parameterMap.getString(KEY_EXCEL_FORMAT));
			HttpServletRequest request=ServiceInstance.getRequest();
			excelConfig.setTemplatePath(request.getRealPath(appConfig.getString(KEY_EXCEL_TEMPLATE_PATH)));		
			String fileName = parameterMap.getString(KEY_FILE_NAME, "excel");
			HttpServletResponse response=ServiceInstance.getResponse();
			response.setCharacterEncoding("GBK");
			if("xls".equals(excelConfig.getFileFormat().toLowerCase())){
				response.setContentType("application/vnd.ms-excel");
				fileName=fileName+".xls";
			}else{
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml");			
				fileName=fileName+".xlsx";
			}
			response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");
			ExcelFactory.createExcel(model, excelConfig,response.getOutputStream());
		}catch(Exception e){
			throw e;
		}finally{
			if(appConfig!=null)
				appConfig.putBoolean("KEY_EXCEL_EXPORT_STATUS",false);
		}
		return EventModel.HANDLE_STOP;
	}
	
	public void preCreateModel(){
		CompositeMap appConfig=ServiceInstance.getApplicationConfig();
		if(appConfig.getBoolean("KEY_EXCEL_EXPORT_STATUS", false))
			throw new RuntimeException("导出程序正在运行,请稍后再试");
		appConfig.putBoolean("KEY_EXCEL_EXPORT_STATUS",true);
		try{
			CompositeMap modelConfig;		
			excelConfig=(ExcelReport)mEngine.getOcManager().createObject(config);
			List keyList=new LinkedList();
			List list=excelConfig.getExcelSheets();
			Iterator sheetIt=list.iterator();
			while(sheetIt.hasNext()){
				ExcelSheet sheetConfig=(ExcelSheet)sheetIt.next();
				List tableList=sheetConfig.getExcelTables();
				Iterator tableIt=tableList.iterator();
				while(tableIt.hasNext()){
					ExcelTable tableConfig=(ExcelTable)tableIt.next();
					keyList.add(tableConfig.getDataModel());
				}
			}
			Iterator it=keyList.iterator();
			while(it.hasNext()){
				String target=(String)it.next();
				modelConfig=CompositeUtil.findChild(ServiceInstance.getModelConfig(),"sql-query","rootpath",target);
				if(modelConfig!=null){
					modelConfig.put("pagesize", 65534);
					modelConfig.put("fetchall", false);
				}else{
					modelConfig=CompositeUtil.findChild(ServiceInstance.getModelConfig(),"query","Target",target);
					if(modelConfig!=null){
						modelConfig.put("PageSize", 65534);					
					}
				}			
			}
		}finally{
			if(appConfig!=null)
				appConfig.putBoolean("KEY_EXCEL_EXPORT_STATUS",false);
		}
	}
}
