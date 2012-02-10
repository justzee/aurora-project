package org.lwap.feature;

import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.mvc.excel.ExcelExport;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.event.EventModel;

import aurora.database.IResultSetConsumer;
import aurora.database.IResultSetProcessor;
import aurora.service.ServiceInstance;

public class ExcelResultSetProcessor implements IResultSetConsumer, IResultSetProcessor{
	MainService service;
	public ExcelResultSetProcessor(CompositeMap context){
		this.service=MainService.getServiceInstance(context);
	}
	public void processResultSet(ResultSet rs) {
		try {
			generateExcel(rs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void generateExcel(ResultSet resultSet) throws Exception{
		CompositeMap model = service.getModel();
        CompositeMap parameter = service.getParameters();       
        CompositeMap column_config = parameter.getChild(ModelCSVExportByClient.Column_config);
        if(column_config==null) throw new IllegalArgumentException("Must pass "+column_config+" property to export CSV data");
        
        //print csv content to response
        HttpServletResponse response = service.getResponse();    
        response.setContentType("application/vnd.ms-excel;charset=GBK");
        String fileName=service.getParameters().getString(ModelCSVExportByClient.KEY_FILE_NAME);        
        response.setHeader("Content-Disposition","attachment; filename=\"" + new String(fileName.getBytes("gb2312"),"ISO8859-1" )+ ".xls\"");        
        Writer out = response.getWriter();        
        Iterator it = column_config.getChildIterator();
        if( it==null ) throw new IllegalArgumentException("No columns defined in "+column_config);
        while(it.hasNext()){                
            CompositeMap item = (CompositeMap)it.next();
            String dataIndex = item.getString(ModelCSVExportByClient.KEY_DATA_INDEX);
            String prompt = item.getString(ModelCSVExportByClient.KEY_PROMPT);
            if(prompt!=null) prompt = service.getLocalizedString(prompt);
            if(dataIndex==null) 
                throw new IllegalArgumentException("Must specify '"+ModelCSVExportByClient.KEY_DATA_INDEX+"' property in column config "+item.toXML());
            if(dataIndex.indexOf('@')>=0)
                item.put(DataBindingConvention.KEY_DATAFIELD, dataIndex);
            else
                item.put(DataBindingConvention.KEY_DATAFIELD, "@"+dataIndex);
            item.put(ModelCSVExportByClient.KEY_PROMPT, prompt);
        }

        ExcelExport dataTable=(ExcelExport)DynamicObject.cast(column_config, ExcelExport.class);
        dataTable.setResultSet(resultSet,model);
        dataTable.setWriter(out);
        dataTable.setCreateTableHead(true);
        dataTable.printTable();        
	}
	public void begin(String root_name) {
		// TODO Auto-generated method stub
		
	}

	public void newRow(String row_name) {
		// TODO Auto-generated method stub
		
	}

	public void loadField(String name, Object value) {
		// TODO Auto-generated method stub
		
	}

	public void endRow() {
		// TODO Auto-generated method stub
		
	}

	public void end() {
		// TODO Auto-generated method stub
		
	}

	public void setRecordCount(long count) {
		// TODO Auto-generated method stub
		
	}

	public Object getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
