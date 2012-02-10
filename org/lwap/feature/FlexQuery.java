/*
 * Created on 2005-12-9
 */
package org.lwap.feature;

import java.io.IOException;

import javax.servlet.ServletException;

import org.lwap.controller.MainService;
import org.lwap.database.DatabaseQuery;
import org.lwap.mvc.excel.ExcelDataTable;
import org.lwap.report.QueryBuilder;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.IterationHandle;
import uncertain.proc.ProcedureRunner;

/**
 * FlexQuery
 * @author Zhou Fan
 * 
 */
public class FlexQuery {
    
	public static final String KEY_QUERY_ID = "query_id";
    private static final String EXCEL_TABLE = "excel-table";
    public static final String KEY_DEFAULT_TARGET = "QUERY_RESULT";    
    
    MainService 	service;
    CompositeMap	context;
	QueryBuilder 	builder;
	
	CompositeMap 	excel_table;
	boolean			is_getdata = false;

    
	void createQueryStatement() throws IOException, ServletException {
		CompositeMap params =service.getParameters();
		CompositeMap model = new CompositeMap();
		service.databaseAccess("fnd_fq_query.data", params, model);
		CompositeMap query = model.getChild("QUERY");
		
		if( query != null) 
		try{
			builder = (QueryBuilder)DynamicObject.cast(query, QueryBuilder.class);
		} catch(Exception ex){
			throw new ServletException(ex);
		}
		else throw new ServletException("can't get query info");
		
	}	    
    
    public void onPrepareService(ProcedureRunner runner) 
    throws Exception {
		context = runner.getContext();
		service = MainService.getServiceInstance(context);
		is_getdata = ExcelDataTable.isGenerateData(service.getRequest());
		// parse query_id parameter
		CompositeMap params = service.getParameters();
		String qid = service.getRequest().getParameter(KEY_QUERY_ID);
		if(qid==null) throw new IllegalArgumentException("query_id is null");
		params.put(KEY_QUERY_ID, new Long(Long.parseLong(qid)));
    }
	
	/**
	 * @see org.lwap.application.BaseService#createModel()
	 */
	public void preCreateModel() throws IOException, ServletException {

		createQueryStatement();
		CompositeMap model_config = service.getModelConfig();
		String sql = builder.createSqlStatement(service.getParameters());

		DatabaseQuery query = DatabaseQuery.createQuery(sql);
		query.setTarget(KEY_DEFAULT_TARGET);
		model_config.addChild(query.getObjectContext());
		service.createModel();

	}
	
	
	/**
	 * @see org.lwap.application.BaseService#createView()
	 */
	public void preCreateView() throws IOException, ServletException {
		
		CompositeMap view = service.getViewConfig();
		
		IterationHandle handle = new IterationHandle(){
    		public int process( CompositeMap map){
    			if(EXCEL_TABLE.equals(map.getName())){
    				excel_table = map;
    				return IterationHandle.IT_BREAK;
    			}
    			else return IterationHandle.IT_CONTINUE;
    		}
		};
		
		view.iterate(handle,true);
		
		if( excel_table == null) throw new ServletException("can't find excel-table in view config");
		builder.createTableColumns(excel_table);
		
		if( builder.isGroupByQuery()) excel_table.put(ExcelDataTable.KEY_TABLE_TYPE, ExcelDataTable.TYPE_PIVOT_TABLE);
		excel_table.put("dataModel", KEY_DEFAULT_TARGET);
		
	}

	public FlexQuery() {

    }

}
