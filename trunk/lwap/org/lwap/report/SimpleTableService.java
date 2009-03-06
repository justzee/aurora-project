/**
 * Created on: 2004-7-15 16:57:40
 * Author:     zhoufan
 */
package org.lwap.report;

import java.io.IOException;

import javax.servlet.ServletException;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

/**
 *  generates a simple table of query result 
 */
public class SimpleTableService extends ReportService {
	
	CompositeMap	table_config = null;

	/**
	 * @see org.lwap.application.BaseService#createView()
	 */
	public void createView() throws IOException, ServletException {
		
		CompositeMap view = this.getViewConfig();
		
		IterationHandle handle = new IterationHandle(){
    		public int process( CompositeMap map){
    			if("table".equals(map.getName())){
    				table_config = map;
    				return IterationHandle.IT_BREAK;
    			}
    			else return IterationHandle.IT_CONTINUE;
    		}
		};
		
		view.iterate(handle,true);
		
		if( table_config == null) 
			table_config = this.getViewBuilderStore().createView("table");
		builder.createTableColumns(table_config);
		table_config.put("dataModel", ReportService.KEY_DEFAULT_TARGET);
/*
		CompositeMap model = new CompositeMap();
		this.databaseAccess("FQ-Query.data", params, model);
		CompositeMap query = model.getChild("QUERY");
*/
		super.createView();
		
	}

}
