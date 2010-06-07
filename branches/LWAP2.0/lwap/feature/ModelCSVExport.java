/*
 * Created on 2006-4-7
 */
package org.lwap.feature;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.AbstractController;
import org.lwap.controller.IController;
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

/**
 * ModelCSVExport: Export data from model to CSV format
 * Usage: Add a line directly under <code><service></code>
 * &lt;model-csv-export /&gt; 
 * @author Zhou Fan
 * 
 */
public class ModelCSVExport extends AbstractController implements IFeature {
    
    public static final String KEY_TABLE = "table";
    
    String  Parameter_name = "generate_csv"; 
    public String	Target;
    public String   SqlType;
    CompositeMap	query_statement;
    CompositeMap	table;
    CompositeMap 	view;
    
    //String  query_name;
    boolean in_generate_state = false;
    
    boolean is_generate = false;
    
    public ModelCSVExport(UncertainEngine engine){
        super(engine);
    }

    public int detectAction( HttpServletRequest request, CompositeMap context ){
        String s = request.getParameter(Parameter_name);
        if("true".equalsIgnoreCase(s))
            in_generate_state = true;
        else
            in_generate_state = false;
        view = ServiceInstance.getViewConfig();        
        return IController.ACTION_NOT_DETECTED;
    }
    
    /**
     * Called by framework to get proper procedure name to run
     * @return procedure name
     */
    public String getProcedureName(){
        return null;
    }
    
    public int attachTo(CompositeMap config, Configuration procConfig ){        
        query_statement = config;
        Target = config.getString("Target");
        SqlType = config.getString("SqlType");
        if(Target==null && SqlType==null){
            System.out.println("[ModelCSVExport] Warning: 'Target' property must be set to a path to dataModel");
            System.out.println(config.toXML());
            return IFeature.NO_FEATURE_INSTANCE;
        }
        else{
            return IFeature.NORMAL;
        }
       
    }
    
    /** disable result set paging */
    public void preCreateModel(ProcedureRunner runner){
        if(!in_generate_state) return;
        query_statement.putBoolean(DatabaseQuery.KEY_PAGE_RESULTSET, false);
    }
    
    public int preBuildOutputContent(ProcedureRunner runner) throws Exception {
        //System.out.println("csv export begin");
        if(!in_generate_state){
            return EventModel.HANDLE_NORMAL;
        }

        CompositeMap model = ServiceInstance.getModel();
        model = (CompositeMap)model.getObject(Target);
        if(model==null) return EventModel.HANDLE_NORMAL;

        /*
        if(view!=null){
            table = CompositeUtil.findChild(view,KEY_TABLE,DataBindingConvention.KEY_DATAMODEL,Target);
        }
        */
        table = CompositeUtil.findChild(ServiceInstance.getServiceContext(),KEY_TABLE,DataBindingConvention.KEY_DATAMODEL,Target);
        if(table==null){ 
            System.out.println("[ModelCSVExport] <table> not found for <query-statement Target='"+Target+"'>");
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
