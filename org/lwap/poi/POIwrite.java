package org.lwap.poi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import uncertain.composite.CompositeMap;

public class POIwrite {
	public static void wirteFile(CompositeMap datamodel,
			CompositeMap tablemodel, File file) throws Exception {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet1");
		int startrow = 0;

		Iterator it = datamodel.getChildIterator();
		while (it.hasNext()) {
			HSSFRow row = sheet.createRow(startrow);
			Iterator columnit = tablemodel.getChildIterator();
			int startcol = 0;
			CompositeMap ct = (CompositeMap) it.next();
			while (columnit.hasNext()) {
				CompositeMap column = (CompositeMap) columnit.next();
				String dataField = column.get("DataIndex").toString()
						.replaceAll("@", "");
				Object data = ct.get(dataField);
				String datavalue = data != null ? data.toString() : "";
				row.createCell(startcol).setCellValue(datavalue);
				startcol++;
			}
			startrow++;
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		wb.write(fileOut);
		fileOut.close();
	}
}
