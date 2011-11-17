package org.lwap.plugin.output;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.TextParser;
import uncertain.core.UncertainEngine;
import uncertain.event.EventModel;
import uncertain.ocm.IConfigurable;
import uncertain.proc.ProcedureRunner;

public class TextOutput implements IConfigurable {
	String KEY_TEXT_CONTENT_TYPE = "text/plain";
	String KEY_ENCODING = "GBK";

	CompositeMap config;
	UncertainEngine mEngine;
	String fileName="data";
	String fileType="txt";
	MainService service;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public TextOutput(UncertainEngine engine) {
		mEngine = engine;
	}

	public int preBuildOutputContent(ProcedureRunner runner) throws Exception {
		Iterator iterator = this.config.getChildIterator();
		service = MainService.getServiceInstance(runner.getContext());
		HttpServletResponse response = service.getResponse();
		response.setCharacterEncoding(KEY_ENCODING);
		response.setContentType(KEY_TEXT_CONTENT_TYPE);
		response.addHeader("Content-Disposition", "attachment; filename=\""
				+ URLEncoder.encode(getFileName(),"UTF-8") + "." + getFileType() + "\"");
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		try {
			while (iterator != null && iterator.hasNext()) {
				CompositeMap childConfig = (CompositeMap) iterator.next();
				Object obj = mEngine.getOcManager().createObject(childConfig);
				process(obj, pw);
			}
		} finally {
			pw.close();
		}

		return EventModel.HANDLE_STOP;
	}

	public void endConfigure() {

	}

	public void beginConfigure(CompositeMap config) {
		this.config = CompositeUtil.attributeNameToLower(config);
	}

	void process(Object obj, PrintWriter pw) {
		CompositeMap model = service.getModel();
		if (obj instanceof TextLabel) {
			pw.println(TextParser.parse(((TextLabel) obj).getValue(), model));
		}else if(obj instanceof TextTable){
			TextTable table=(TextTable)obj;
			CompositeMap columns=table.getColumn();			
			List colList=columns.getChildsNotNull();
			if(table.getCreatetablehead()){					
				for(int i=0, l=colList.size();i<l;i++){
					CompositeMap column=(CompositeMap)colList.get(i);
					pw.print(TextParser.parse(column.getString("prompt"), model));
					pw.print(table.getSeparator());
				}				
			}
			CompositeMap data=(CompositeMap)model.getObject(table.getDatamodel());
			Iterator it=data.getChildIterator();
			while(it!=null&&it.hasNext()){
				CompositeMap record=(CompositeMap)it.next();
				for(int i=0, l=colList.size();i<l;i++){
					CompositeMap column=(CompositeMap)colList.get(i);
					pw.print(record.getObject(column.getString("valuefield")));
					pw.print(table.getSeparator());
				}
				pw.println();
			}			
		}
//		pw.flush();
	}
}
