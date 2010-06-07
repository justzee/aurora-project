package org.lwap.plugin.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import jxl.Cell;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

import uncertain.composite.CompositeMap;

public class PareExcel {
	public CompositeMap pareExcel(File f) throws Exception{
		CompositeMap dataMap=new CompositeMap();
		String fileName=f.getName();
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		if (".xls".equalsIgnoreCase(suffix.toLowerCase())) {
			return pareExcel2003(f,dataMap);
		}
		return dataMap;
	}
	public CompositeMap pareExcel(File f,CompositeMap dataMap) throws Exception{
		String fileName=f.getName();
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		if (".xls".equalsIgnoreCase(suffix.toLowerCase())) {
			return pareExcel2003(f,dataMap);
		}
		return dataMap;
	}	
	public CompositeMap pareExcel2003(File f,CompositeMap dataMap) throws Exception{		
		InputStream is = null;
		FileInputStream fn = null;	
		String contentString;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			fn = new FileInputStream(f);
			is = fn;
			Workbook rb = Workbook.getWorkbook(is);
			Sheet s = rb.getSheet(0);			
			for(int i=0,rs = s.getRows();i<rs;i++){
				CompositeMap item=new CompositeMap("row");
				Cell[] c=s.getRow(i);
				for(int j=0,cs=c.length;j<cs;j++){
					contentString=c[j].getContents();
					contentString=contentString==null?"":contentString.trim();
					if ("jxl.read.biff.DateRecord".equals(c[j].getClass().getName())) {
						DateCell dr = (DateCell)c[j];
						contentString = sdf.format(dr.getDate());
					} else if("jxl.read.biff.NumberRecord".equals(c[j].getClass().getName())){
						NumberCell nc = (NumberCell)c[j];
						contentString = Double.toString(nc.getValue());
					}
					item.put("cell"+j, contentString==null?"":contentString);
				}
				dataMap.addChild(item);
			}			
		}finally {
			try {
				fn.close();
				is.close();
			} catch (Exception e) {
				throw e;
			}
		}
		return dataMap;
	}
	public CompositeMap pareExcel2007(File f){
		CompositeMap dataMap=new CompositeMap();
		return dataMap;
	}	
}
