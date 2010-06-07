package org.lwap.feature;

import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.lwap.database.DatabaseQuery;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.mvc.excel.ExcelDataTable;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.DynamicObject;
import uncertain.core.UncertainEngine;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

public class StaticModelCSVExport extends ModelCSVExport {	
	String source_model;

	public StaticModelCSVExport(UncertainEngine engine) {
		super(engine);
		// TODO Auto-generated constructor stub
	}

	public int attachTo(CompositeMap config, Configuration procConfig) {
		source_model=config.getString(DataBindingConvention.KEY_DATAMODEL);
		return IFeature.NORMAL;
	}

	public void preCreateModel(ProcedureRunner runner) {
		return;
	}
	public int preBuildOutputContent(ProcedureRunner runner) throws Exception {
        //System.out.println("csv export begin");
        if(!in_generate_state){
            return EventModel.HANDLE_NORMAL;
        }
        CompositeMap model = ServiceInstance.getModel();
        model = (CompositeMap)model.getObject(source_model);
        if(model==null) return EventModel.HANDLE_NORMAL;      
        table = CompositeUtil.findChild(ServiceInstance.getServiceContext(),KEY_TABLE,DataBindingConvention.KEY_DATAMODEL,source_model);
        if(table==null){ 
            System.out.println("[ModelCSVExport] <table> not found for <jsp:excel-export-check exportModel='"+source_model+"'>");
            //System.out.println(ServiceInstance.getServiceContext().toXML());
            return EventModel.HANDLE_NORMAL;
        }
        ServiceInstance.localizeView();
        //print csv content to response
        HttpServletResponse response = ServiceInstance.getResponse();
        response.setContentType("application/vnd.ms-excel;charset=gbk");
        
        Writer out = response.getWriter();
        if(model.getChildIterator() ==null){
            out.write("Empty model");
        }
        else{
        ExcelDataTable excel_table = (ExcelDataTable)DynamicObject.cast(table,ExcelDataTable.class);
        //excel_table.setSeparatorChar(',');
        excel_table.setModel(model);
        excel_table.setWriter(out);
        excel_table.setCreateTableHead(true);
        excel_table.printTable();
            //System.out.println("end generating");
        }
        return EventModel.HANDLE_STOP;
    }
}
