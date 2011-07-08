package org.lwap.plugin.excel;

import java.io.File;
import java.io.OutputStream;

import org.lwap.plugin.poi.ExcelFactoryImpl;
import uncertain.composite.CompositeMap;

public class ExcelFactory {
	public static void createExcel(CompositeMap data, ExcelReport config,OutputStream os) throws Exception{
		new ExcelFactoryImpl().createExcel(data, config,os);
	}

	public static CompositeMap extractionExcel(File file) throws Exception{
		return new ExcelFactoryImpl().extractionExcel(file);
	}
}
