package org.lwap.plugin.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lwap.plugin.excel.ExcelLabel;
import org.lwap.plugin.excel.ExcelReport;
import org.lwap.plugin.excel.ExcelSheet;
import org.lwap.plugin.excel.ExcelTable;
import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

public class ExcelFactoryImpl {
	CompositeMap dataModel;
	public File createExcel(CompositeMap dataModel, ExcelReport config)throws Exception {
		File f = null;
		Workbook wb = null;
		Sheet sheet = null;
		ArrayList<ExcelSheet> sheetConfigs = null;
		ExcelSheet sheetConfig = null;
		ArrayList<ExcelTable> tableConfigs = null;
		ArrayList<ExcelLabel> labelConfigs = null;
		InputStream is = null;
		this.dataModel = dataModel;
		boolean is_xls = "xls".equalsIgnoreCase(config.getFileFormat()) ? true
				: false;
		String template=config.getTemplate();	
		if(template!=null)
			template = is_xls? template + ".xlt" : config
				.getTemplate() + ".xlsx";
		String templatePath=config.getTemplatePath();	
		File filePath=new File(templatePath);
		if(!filePath.exists()&&template!=null)
			throw new Exception("excel template path undefined");
		try {			
			File file = new File(filePath,template);
			is = new FileInputStream(file);
		} catch (Exception e) {
			if(template!=null)
				throw new Exception("excel template not exist");
		}
		if (is != null)
			wb = is_xls ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
		else
			wb = is_xls ? new HSSFWorkbook() : new XSSFWorkbook();
		sheetConfigs = config.getExcelSheets();
		for (int i = 0, size = sheetConfigs.size(); i < size; i++) {
			sheetConfig = sheetConfigs.get(i);
			if ((sheet = wb.getSheet(sheetConfig.getTitle())) == null)
				sheet = wb.createSheet(sheetConfig.getTitle());
			tableConfigs = sheetConfig.getExcelTables();
			for (int j = 0, l = tableConfigs.size(); j < l; j++) {
				createExcelTable(sheet, tableConfigs.get(j));
			}
			labelConfigs = sheetConfig.getExcelLabels();
			for (int j = 0, l = labelConfigs.size(); j < l; j++) {
				createExcelLabel(sheet, labelConfigs.get(j));
			}
		}
		File tempPath = null;
		try{
			tempPath = new File(config.getTempPath());
			if(!tempPath.exists())
				throw new Exception();
		}catch (Exception e) {
			throw new Exception("temp path undefined");
		}			
		String filename = "报表" + String.valueOf(System.currentTimeMillis())
				+ ".xls";
		if (!is_xls) {
			filename = filename + "x";
		}
		f = new File(tempPath,filename);
		FileOutputStream out = new FileOutputStream(f);
		try{		
			wb.write(out);	
		}finally{
			if(out!=null)
				out.close();
		}
		return f;
	}

	void createExcelTable(Sheet sheet, ExcelTable tableConfig) {
		Row r = null;
		Cell c = null;
		boolean createTableHead = tableConfig.getCreateTableHead();
		boolean tableHeadEachRow = tableConfig.getTableHeadEachRow();
		int rownum = tableConfig.getRow();
		int colnum = tableConfig.getCol();
		CompositeMap columnConfigs = tableConfig.getColumns();
		CompositeMap tabledata = dataModel.getChild(tableConfig.getDataModel());
		int count = -1;
		if (createTableHead) {
			if ((r = sheet.getRow(rownum + count)) == null)
				r = sheet.createRow(rownum + count);
			createExcelTableHead(r, columnConfigs, colnum);
			count++;
		}
		Iterator it = tabledata.getChildIterator();
		if (it != null) {
			while (it.hasNext()) {
				if ((r = sheet.getRow(count + rownum)) == null)
					r = sheet.createRow(count + rownum);
				if (tableHeadEachRow && count % 2 == 1) {
					createExcelTableHead(r, columnConfigs, colnum);
				} else {
					CompositeMap record = (CompositeMap) it.next();
					Iterator iterator = columnConfigs.getChildIterator();
					if (iterator != null) {
						int j = -1;
						while (iterator.hasNext()) {
							CompositeMap object = (CompositeMap) iterator
									.next();
							if ((c = r.getCell(j + colnum)) == null)
								c = r.createCell(j + colnum);
							c.setCellValue(record.getString(object.getString(
									"datafield").replace("@", "")));
							j++;
						}
					}
				}
				count++;
			}
		}
	}

	void createExcelTableHead(Row r, CompositeMap columnConfigs, int colnum) {
		Cell c = null;
		Iterator iterator = columnConfigs.getChildIterator();
		if (iterator != null) {
			int j = -1;
			while (iterator.hasNext()) {
				CompositeMap object = (CompositeMap) iterator.next();
				if ((c = r.getCell(j + colnum)) == null)
					c = r.createCell(j + colnum);
				c.setCellValue(object.getString("prompt"));
				j++;
			}
		}
	}

	void createExcelLabel(Sheet sheet, ExcelLabel labelConfig) {
		int rownum = labelConfig.getRow(), colnum = labelConfig.getCol();
		String value = TextParser.parse(labelConfig.getContent(), dataModel);
		Row r = sheet.getRow(rownum - 1);
		Cell c = null;
		if (r == null)
			r = sheet.createRow(rownum - 1);
		if ((c = r.getCell(colnum - 1)) == null)
			c = r.createCell(colnum - 1);
		c.setCellValue(value);
	}

	public CompositeMap extractionExcel(File file) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String suffix = file.getName().substring(
				file.getName().lastIndexOf("."));
		InputStream is = null;
		Workbook wb = null;
		Sheet sheet = null;
		Row row = null;
		Cell cell = null;
		CompositeMap excelData = new CompositeMap("excel-data");
		is = new FileInputStream(file);
		if (".xls".equalsIgnoreCase(suffix)) {
			wb = new HSSFWorkbook(is);
		} else if (".xlsx".equalsIgnoreCase(suffix)) {
			wb = new XSSFWorkbook(is);
		}		
		for (int i = 0, sheetLength = wb.getNumberOfSheets(); i < sheetLength; i++) {
			sheet = wb.getSheetAt(i);
			CompositeMap sheetData = new CompositeMap("sheet");
			sheetData.put("name", sheet.getSheetName());
			for (int firstRow = sheet.getFirstRowNum(), lastRow = sheet
					.getLastRowNum(); firstRow <= lastRow; firstRow++) {
				row = sheet.getRow(firstRow);
				CompositeMap rowData = new CompositeMap("row");
				if (row != null) {
					for (int firstCell = row.getFirstCellNum(), lastCell = row
							.getLastCellNum(); firstCell < lastCell; firstCell++) {
						cell = row.getCell(firstCell);
						if (cell != null) {
							if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
								if (DateUtil.isCellDateFormatted(cell))
									rowData.put("C" + firstCell, sdf
											.format(cell.getDateCellValue()));
								else
									rowData.put("C" + firstCell, cell
											.getNumericCellValue());
							} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								rowData.put("C" + firstCell, cell.toString());
							} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {

							} else {
								rowData.put("C" + firstCell, "undefined");
							}
						}
					}
				}
				sheetData.addChild(rowData);
			}
			excelData.addChild(sheetData);
		}
		return excelData;
	}
}
