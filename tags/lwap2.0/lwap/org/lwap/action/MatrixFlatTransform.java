/*
 * Created on 2009-3-19
 */
package org.lwap.action;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.ConfigurationError;
import uncertain.proc.AbstractEntry;
import uncertain.proc.ProcedureRunner;
import uncertain.util.StringSplitter;

public class MatrixFlatTransform extends AbstractEntry {

    /** path to get a CompositeMap instance containing dynamic columns */
    public String Column_config_path;
    
    /** path to get source data */
    public String Source_data_path;
    
    /** field for name of column in column config */
    public String Column_field_name;
    
    /** name for key field in generated record */
    public String Key_name="key";
    
    /** name for value field in generated record */
    public String Value_name="value";
    
    /** fields to put into sub item from parent item */
    public String Key_fields;
    
    public MatrixFlatTransform() {
    }

    public void run(ProcedureRunner runner) throws Exception {
        if(Column_field_name==null) throw new ConfigurationError("Must set field_name property");
        CompositeMap    context = runner.getContext();
        //ILogger logger = LogManager.getLogger(getClass().getName(), context);
        
        CompositeMap    column_config = (CompositeMap)context.getObject(Column_config_path);
        if(column_config==null)
            throw new IllegalArgumentException("Can't get column config");
        Object[][] field_names = CompositeUtil.toArray( column_config, new String[]{Column_field_name});
        //logger.log("get fields "+field_names.length);
        
        CompositeMap    source = (CompositeMap)context.getObject(Source_data_path);
        if(source==null){
            //logger.log("source data is empty");
            return;
        }
        
        String[] key_fields = null;
        if(Key_fields!=null)
            key_fields=StringSplitter.split2(Key_fields, ",");
        
        List childs =  source.getChilds();
        if( childs==null ) return;
        ListIterator source_it = childs.listIterator();
        
        List new_childs = new LinkedList();
        while(source_it.hasNext()){
            CompositeMap item = (CompositeMap)source_it.next();            
            for(int i=0; i<field_names.length; i++){
                CompositeMap m = new CompositeMap("item");
                if(key_fields!=null){
                    for(int n=0; n<key_fields.length; n++)
                        m.put(key_fields[n], item.get(key_fields[n]));
                }
                String field_name = (String)field_names[i][0];
                Object value = item.get(field_name);                
                m.put(Key_name,field_name);
                m.put(Value_name, value);
                item.remove(field_name);
                //item.addChild(m);
                new_childs.add(m);
            }
            source_it.remove();
        }
        source.addChilds(new_childs);
        
    }

}
