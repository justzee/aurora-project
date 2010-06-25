package org.lwap.plugin.excel;

import java.util.ArrayList;
public class ExcelReport {
	String template;
	String templatePath="";
	String tempPath="";
	boolean autoHide=false;
	String fileFormat="xls";//xlsx or xls .default xls
	ArrayList<ExcelSheet> excelSheets=new ArrayList<ExcelSheet>();
	
	public String getTempPath() {
		return tempPath;
	}
	public void setTempPath(String tempPath) {
		if(tempPath!=null)
			this.tempPath = tempPath;
	}
	public String getTemplatePath() {
		return templatePath;
	}
	public void setTemplatePath(String templatePath) {
		if(templatePath!=null)
			this.templatePath = templatePath;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		if(template!=null)
			this.template = template;
	}
	public String getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(String fileFormat) {
		if(fileFormat!=null)
			this.fileFormat = fileFormat;
	}
	public ArrayList<ExcelSheet> getExcelSheets() {
		return excelSheets;
	}
	public void addExcelSheet(ExcelSheet excelSheet) {
		this.excelSheets.add(excelSheet);
	}
	public boolean getAutoHide() {
		return autoHide;
	}
	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}	
}