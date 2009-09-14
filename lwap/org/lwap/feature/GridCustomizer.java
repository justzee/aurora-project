/*
 * Project: DianGuanJia
 * 
 * Copyright(c) 2009 www.dianguanjia.cn
 * All rights reserved.
 */
package org.lwap.feature;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.logging.ILogger;
import uncertain.ocm.IObjectRegistry;
import uncertain.proc.ProcedureRunner;
import aurora.database.FetchDescriptor;
import aurora.database.service.DatabaseServiceFactory;
import aurora.database.service.RawSqlService;
import aurora.database.service.SqlServiceContext;

public class GridCustomizer {

	private static final String GRID_CUSTOMIZER_SERVICE = "sys.load_custom_grid";
	
	private static final String COLUMN_VISIABLE = "VISIABLE";
	private static final String COLUMN_DATA_INDEX = "DATA_INDEX";
	private static final String COLUMN_ORDER = "ORDER_NUM";
	private static final String COLUMN_WIDTH = "WIDTH";

	private IObjectRegistry mRegistry;

	private ILogger mLogger;

	private DatabaseServiceFactory mSvcFactory;

	private DataSource dataSource;

	public GridCustomizer(IObjectRegistry registry, DatabaseServiceFactory factory, DataSource ds) {
		mRegistry = registry;
		mSvcFactory = factory;
		dataSource = ds;
	}

	public void onPopulateView(ProcedureRunner runner, IObjectRegistry registry, DatabaseServiceFactory factory, DataSource ds) throws Exception {
		CompositeMap context = runner.getContext();
		MainService service = MainService.getServiceInstance(context);
		CompositeMap view = service.getView();

		processCustomeGrid(context, service.getServiceName(), view);
	}

	/**
	 * 
	 * @param serviceName
	 * @param view
	 * @throws Exception
	 */
	private void processCustomeGrid(CompositeMap context, String serviceName, CompositeMap view) throws Exception {
		Connection conn = dataSource.getConnection();
		try {
			List grids = CompositeUtil.findChilds(view, "grid");
			Iterator it = grids.iterator();
			while (it.hasNext()) {
				CompositeMap grid = (CompositeMap) it.next();
				String gridId = grid.getString("Id");
				if (gridId != null) {
					SqlServiceContext sqlServiceContext = SqlServiceContext.createSqlServiceContext(conn);
					sqlServiceContext.getParameter().put("userId", context.getObject("/session/@user_id"));
					sqlServiceContext.getParameter().put("gridId", gridId.toUpperCase());
					sqlServiceContext.getParameter().put("service", serviceName.toUpperCase());
					RawSqlService sqlService = mSvcFactory.getSqlService(GRID_CUSTOMIZER_SERVICE);
					CompositeMap resultMap = sqlService.queryAsMap(sqlServiceContext, FetchDescriptor.getDefaultInstance());
					List childs = new ArrayList();
					if (resultMap != null && resultMap.getChilds() != null)
					childs = resultMap.getChilds();
					
					String columnConfig = grid.getString("ColumnConfig", "");
					if(!"".equals(columnConfig)) {
						CompositeMap cols = (CompositeMap)context.getObject(columnConfig);
						List list = cols.getChilds();
						if(list != null){
							Iterator sit = list.iterator();
							while(sit.hasNext()){
								CompositeMap scol = (CompositeMap) sit.next();
								String serviceDataIndex = scol.getString(COLUMN_DATA_INDEX, "");
								if(childs != null && childs.size() >0){
									Iterator pit = childs.iterator();
									while(pit.hasNext()){
										CompositeMap col = (CompositeMap) pit.next();
										String dataIndex = col.getString(COLUMN_DATA_INDEX);
										if(serviceDataIndex.equalsIgnoreCase(dataIndex)) {
											col.put(COLUMN_VISIABLE, scol.getString(COLUMN_VISIABLE));
											col.put(COLUMN_ORDER, scol.getInt(COLUMN_ORDER));
											col.put(COLUMN_WIDTH, scol.getInt(COLUMN_WIDTH));											
											break;
										}
									}
								}else{
									childs.addAll(list);									
								}								
							}							
						}
					}
					
					configGrid(grid, childs);
				}
			}
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private void configGrid(CompositeMap grid, List cfgs) {
		List columns = CompositeUtil.findChilds(grid, "column");
		Iterator cit = cfgs.iterator();
		while (cit.hasNext()) {
			CompositeMap col = (CompositeMap) cit.next();
			String dataIndex = col.getString(COLUMN_DATA_INDEX);
			Iterator it = columns.iterator();
			while (it.hasNext()) {
				CompositeMap column = (CompositeMap) it.next();
				String colDataIndex = column.getString("DataIndex");
				if (colDataIndex != null && colDataIndex.equalsIgnoreCase(dataIndex)) {
					configGridColumn(column, col);
					break;
				}
			}
		}
		CompositeMap cols = CompositeUtil.findChild(grid, "columns");
		Collections.sort(cols.getChilds(), new Comparator() {
			 public int compare(Object o1, Object o2) {
				 CompositeMap m1 = (CompositeMap) o1;
				 CompositeMap m2 = (CompositeMap) o2;
				 Object order1 = m1.get("Order");
				 Object order2 = m2.get("Order");
				 if (order1 == null || order2 == null) {
					 return 0;
				 } else {
					 int orderInt1 = Integer.parseInt(order1.toString());
					 int orderInt2 = Integer.parseInt(order2.toString());
					 return orderInt1 > orderInt2 ? 1 : 0;
				 }
			 }
			 public boolean equals(Object obj) {
				 return this.equals(obj);
			 }
		});
	}

	private void configGridColumn(CompositeMap column, CompositeMap cfg) {
		String visiable = cfg.getString(COLUMN_VISIABLE).toUpperCase();
		Integer order = cfg.getInt(COLUMN_ORDER);
		column.put("Order", order);
		if (!"Y".equals(visiable)) {
			column.put("Hidden", "true");
		} else {
			column.put("Hidden", "false");
			Integer width = cfg.getInt(COLUMN_WIDTH);
			column.put("Width", width);
		}
	}
}
