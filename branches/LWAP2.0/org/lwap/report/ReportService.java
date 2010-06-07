/**
 * Created on: 2004-7-15 16:52:27
 * Author:     zhoufan
 */
package org.lwap.report;

import java.io.IOException;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;
import org.lwap.database.DatabaseQuery;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 * Abstract base class that generate model from FQ-Query.data
 */
public abstract class ReportService extends BaseService {
	
	public static final String KEY_DEFAULT_TARGET = "QUERY_RESULT";	
	
	QueryBuilder builder;
	CompositeMap query_config;
	
	/**
	 * @see org.lwap.application.BaseService#preService()
	 */
	public void preService() throws IOException, ServletException {
		CompositeMap params = getParameters();
		CompositeMap model = new CompositeMap();
		this.databaseAccess("FQ-Query.data", params, model);
		query_config = model.getChild("QUERY");
		
		if( query_config != null) 
		try{
			builder = (QueryBuilder)DynamicObject.cast(query_config, QueryBuilder.class);
		} catch(Exception ex){
			throw new ServletException(ex);
		}
		else throw new ServletException("can't get query info");
		
	}

	/**
	 * @see org.lwap.application.BaseService#createModel()
	 */
	public void createModel() throws IOException, ServletException {
		CompositeMap model_config = this.getModelConfig();
		String sql = builder.createSqlStatement(this.getParameters());

		DatabaseQuery query = DatabaseQuery.createQuery(sql);
		query.setTarget(KEY_DEFAULT_TARGET);
		query.setPageResultset(false);
		model_config.addChild(query.getObjectContext());
		super.createModel();
		this.getModel().addChild(query_config);
	}
	

}
