package org.lwap.plugin.excel;

import java.util.ArrayList;
public class ExcelSheet {	
	String title;
	ArrayList<ExcelTable> excelTables=new ArrayList<ExcelTable>();
	ArrayList<ExcelLabel> excelLabels=new ArrayList<ExcelLabel>();		
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<ExcelTable> getExcelTables() {
		return excelTables;
	}	
	public ArrayList<ExcelLabel> getExcelLabels() {
		return excelLabels;
	}	
	public void addExcelTable(ExcelTable excelTable) {
		this.excelTables.add(excelTable);
	}	
	public void addExcelLabel(ExcelLabel excelLabel) {
		this.excelLabels.add(excelLabel);
	}	
}
