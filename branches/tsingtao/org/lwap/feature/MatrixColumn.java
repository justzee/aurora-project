/*
 * Created on 2005-11-18
 */
package org.lwap.feature;

import java.util.Iterator;
import java.util.List;

import org.lwap.controller.MainService;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.ui.UIAttribute;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;
import uncertain.composite.transform.MatrixTransformer;
import uncertain.event.Configuration;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

/**
 * MatrixColumn
 * @author Zhou Fan
 * 
 */
public class MatrixColumn implements IFeature {
    
    static final String SUMMARY_FUNCTION = "SummaryFunction";
    
    CompositeMap	column_config;
    CompositeMap	table_config;
    CompositeMap    columns_list;

    /**
     * 
     */
    public MatrixColumn() {
    }

    /**
     * 
     */
    public int attachTo(CompositeMap config, Configuration procConfig) {
        column_config = config;
        table_config=column_config.getParent();
        columns_list = table_config;
        if("columns".equalsIgnoreCase(table_config.getName())){            
            table_config = table_config.getParent();
        }
        return IFeature.NORMAL;
    }
    
    public void postCreateModel(ProcedureRunner runner){
        
        CompositeMap	context = runner.getContext();
        MainService		service = MainService.getServiceInstance(context);
        CompositeMap table_model = DataBindingConvention.getDataModel(service.getModel(),table_config);
        /*
        System.out.println("table config:"+table_config.toXML());
        System.out.println("column config:"+column_config.toXML());
        */
        int col_index = columns_list.getChilds().indexOf(column_config);
        if(col_index<0){
            System.out.println("matrix column:aleady transformed");
            return;
        }
        columns_list.getChilds().remove(col_index);
        boolean has_childs = column_config.getChilds()!=null;
        
        if(table_model!=null){
	        MatrixTransformer transformer = new  MatrixTransformer(column_config);
	        if(has_childs)
	            transformer.setFieldValueOnly(false);
            transformer.setCreateMetaData(false);
	        transformer.transform(table_model);
	        List columns = transformer.getColumns();
	        String column_value = null;
	        for(Iterator it = columns.iterator(); it.hasNext();){
	            CompositeMap new_column = (CompositeMap)column_config.clone();
	            new_column.setName("column");
	            CompositeMap source_column = (CompositeMap)it.next();
		        Object o = transformer.getColumnValue(source_column);
		        column_value = o==null?null:o.toString();

		        if(has_childs){
		           new_column.put(DataBindingConvention.KEY_DATAMODEL,"@"+column_value);
		           String[] valueFields = transformer.getColumnValueFields();
		           if(valueFields!=null)
		               new_column.put(DataBindingConvention.KEY_DATAFIELD,"@"+column_value+"/@"+valueFields[0]);
	            }else{
	                new_column.put(DataBindingConvention.KEY_DATAFIELD,"@"+column_value);
	            }

	            String prompt = new_column.getString(UIAttribute.ATTRIB_PROMPT);
	            if(prompt!=null){
	                //column.put()
	                new_column.put(UIAttribute.ATTRIB_PROMPT, TextParser.parse(prompt,source_column));
	            }
                columns_list.getChilds().add(col_index++,new_column);
	        }
        }

    }

}
