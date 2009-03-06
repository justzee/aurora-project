/**
 * Created on: 2003-9-27 20:35:02
 * Author:     zhoufan
 */
package org.lwap.mvc.excel;

import java.io.IOException;

import javax.servlet.ServletException;

import org.lwap.database.DatabaseQuery;
import org.lwap.report.QueryBuilder;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.composite.IterationHandle;

/**
 * 
 */
public class FlexQueryService extends ExcelExportService {
	
	public static final String KEY_DEFAULT_TARGET = "QUERY_RESULT";
	
	QueryBuilder builder;
	CompositeMap excel_table;
	
	/**
	 * @see org.lwap.application.BaseService#preService()
	 */
/*
	public void preService() throws IOException, ServletException {
		CompositeMap params = getParameters();
		CompositeMap model = new CompositeMap();
		this.databaseAccess("FQ-Query.data", params, model);
		CompositeMap query = model.getChild("QUERY");
		
		if( query != null) 
		try{
			builder = (QueryBuilder)DynamicObject.cast(query, QueryBuilder.class);
		} catch(Exception ex){
			throw new ServletException(ex);
		}
		else throw new ServletException("can't get query info");
		
	}	
*/

	void createQueryStatement() throws IOException, ServletException {
		CompositeMap params = getParameters();
		CompositeMap model = new CompositeMap();
		this.databaseAccess("FQ-Query.data", params, model);
		CompositeMap query = model.getChild("QUERY");
		
		if( query != null) 
		try{
			builder = (QueryBuilder)DynamicObject.cast(query, QueryBuilder.class);
		} catch(Exception ex){
			throw new ServletException(ex);
		}
		else throw new ServletException("can't get query info");
		
	}	
	
	/**
	 * @see org.lwap.application.BaseService#createModel()
	 */
	public void createModel() throws IOException, ServletException {
		createQueryStatement();
		CompositeMap model_config = this.getModelConfig();
		String sql = builder.createSqlStatement(this.getParameters());

		DatabaseQuery query = DatabaseQuery.createQuery(sql);
		query.setTarget(KEY_DEFAULT_TARGET);
		model_config.addChild(query.getObjectContext());
		super.createModel();
//		System.out.println(getModel().toXML());

	}
	
	
	/**
	 * @see org.lwap.application.BaseService#createView()
	 */
	public void createView() throws IOException, ServletException {
		
		CompositeMap view = this.getViewConfig();
		
		IterationHandle handle = new IterationHandle(){
    		public int process( CompositeMap map){
    			if("excel-table".equals(map.getName())){
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

		super.createView();

		
	}

}
