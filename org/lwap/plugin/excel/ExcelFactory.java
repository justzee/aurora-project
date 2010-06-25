package org.lwap.plugin.excel;

import java.io.File;
import org.lwap.plugin.poi.ExcelFactoryImpl;
import uncertain.composite.CompositeMap;

public class ExcelFactory {
	public static File createExcel(CompositeMap data, ExcelReport config) throws Exception{
		return new ExcelFactoryImpl().createExcel(data, config);
	}

	public static CompositeMap extractionExcel(File file) throws Exception{
		return new ExcelFactoryImpl().extractionExcel(file);
	}
}
