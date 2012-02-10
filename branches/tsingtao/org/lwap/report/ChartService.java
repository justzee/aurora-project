/**
 * Created on: 2004-7-22 16:59:56
 * Author:     zhoufan
 */
package org.lwap.report;


import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.lwap.mvc.servlet.JspViewFactory;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;

/**
 * 
 */
public class ChartService extends ReportService {
	
	public static final String CHART_VIEW_NAME = "webchart";
	public static final String KEY_CHART_TYPE  = "CHART_TYPE";
	public static final String KEY_CHART_LENGTH  = "CHART_LENGTH";
	public static final String KEY_CHART_WIDTH  = "CHART_WIDTH";		
	public static final String KEY_CHART_FIELD_TYPE  = "CHART_FIELD_TYPE";	
	
	public static final int	TYPE_VALUE  = 1;
	public static final int	TYPE_PARAM1 = 10;
	public static final int	TYPE_PARAM2 = 11;
	
	
	CompositeMap	chart_config = null;
	String			chart_type;

	/**
	 * @see org.lwap.application.BaseService#createView()
	 */
	public void createView() throws IOException, ServletException {
		
		CompositeMap view = this.getViewConfig();
		
		IterationHandle handle = new IterationHandle(){
    		public int process( CompositeMap map){
    			if(CHART_VIEW_NAME.equals(map.getName())){
    				chart_config = map;
    				return IterationHandle.IT_BREAK;
    			}
    			else return IterationHandle.IT_CONTINUE;
    		}
		};
		
		view.iterate(handle,true);
		
		if( chart_config == null) 
			chart_config = JspViewFactory.createView(CHART_VIEW_NAME);
		chart_config.put("dataModel", ReportService.KEY_DEFAULT_TARGET);
		populateChart();
		//System.out.println(chart_config.toXML());
		super.createView();
		
	}
	
	public void populateChart(){
		//System.out.println(query_config.toXML());
		chart_type = query_config.getString(KEY_CHART_TYPE);
		if( chart_type == null) throw new IllegalArgumentException("CHART_TYPE property not set");
		chart_config.put("ChartType", chart_type);

		Object o = query_config.get(KEY_CHART_WIDTH);
		if( o!=null) chart_config.put("Width", o);

		 o = query_config.get(KEY_CHART_LENGTH);
		if( o!=null) chart_config.put("Length", o);		
		
		int n=0;
		Iterator it = query_config.getChild("FIELD-LIST").getChildIterator();
		while(it.hasNext()){
			CompositeMap field = (CompositeMap)it.next();
			int ftype = field.getInt(KEY_CHART_FIELD_TYPE,0);
			String fname = "F" + n++;
			switch(ftype){
				case TYPE_VALUE:
					chart_config.put("Data", fname);
					if("xy".equals(chart_type))
						chart_config.put("YTitle",field.get("FIELD_TITLE"));
					break;
				case TYPE_PARAM1:
					chart_config.put("Param1", fname);
					if("xy".equals(chart_type))
						chart_config.put("XTitle",field.get("FIELD_TITLE"));					
					break;
				case TYPE_PARAM2:
					chart_config.put("Param2", fname);
					break;
			}
		}
		if( chart_config.get("Param2") == null)
			chart_config.put("Param2", chart_config.get("Param1"));
	}

	

}
