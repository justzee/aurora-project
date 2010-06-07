/**
 * Created on: 2003-9-27 17:57:04
 * Author:     zhoufan
 */
package org.lwap.report;

import org.lwap.database.*;

import uncertain.composite.*;

import java.util.*;
 
/**
 * 
 */
public class QueryBuilder extends DynamicObject {
    
    Object getQueryValue( CompositeMap item){
        if( item.get("VARCHAR2_VALUE")!=null) return item.get("VARCHAR2_VALUE");
        else if ( item.get("NUMBER_VALUE")!=null) return item.get("NUMBER_VALUE");
        else if ( item.get("DATE_VALUE")!=null) return item.get("DATE_VALUE");
        else {
            System.out.println("[QueryBuilder] warning: can't find query value :" + item.toXML());
            return null;
        }
    }
    
    String getParamName( String id ){
        return "PARAM" + id;
    }
    
    String getFieldName( CompositeMap fld){
        String sf = fld.getString("SUMMARY_FUNCTION");
        String name = fld.getString("FIELD_NAME");
        if( sf != null){
            if( sf.equalsIgnoreCase("count")) return "count(*)";
            else return sf + "(" + name + ")";
        }else return name;
    }
    
    String getFieldAlias( CompositeMap fld){
        String sf = fld.getString("SUMMARY_FUNCTION");
        String name = fld.getString("FIELD_NAME");
        if( sf != null){
            if( sf.equalsIgnoreCase("count")) return "TOTAL_COUNT";
            else return sf + "_" + name ;
        }else return name;
    }
    
    List getChildSection(String name, boolean nullable ){
        CompositeMap map = this.object_context.getChild(name);
        if( map == null ){ 
          if( !nullable) throw new IllegalArgumentException("required section " + name + " not found in config");
          else return null;
        }
        
        List childs = map.getChilds();
        if( childs == null ) {
          if(!nullable) throw new IllegalArgumentException("required section " + name + " is empty");
          else return null;
        }
        else return childs;     
    }   
    
    public String createSqlStatement(CompositeMap params){
        return createSqlStatement(params,true);
    }
    
    void appendConditionPart(StringBuffer buf, CompositeMap item, String key){
        String str = item.getString(key);
        if(str!=null)
            buf.append(str).append(' ');
    }
    
    public String createSqlStatement( CompositeMap params, boolean sequence_field_alias ){

        boolean is_group_by = this.isGroupByQuery();
        
        SQLSelectStatement stmt = new SQLSelectStatement( getString("QUERY_OBJECT") );
        String sql_type = getString("SQL_TYPE");

        List fields = getChildSection("FIELD-LIST", false);     
        for( int i=0; i<fields.size(); i++){
            CompositeMap item = (CompositeMap)fields.get(i);
            // System.out.println(item.toXML());
            String name  = getFieldName(item);
            String alias = sequence_field_alias?"F" + i : '"'+item.getString("FIELD_TITLE")+'"';
            stmt.addField(name,alias);              
            if( is_group_by && item.get("SUMMARY_FUNCTION") == null) stmt.addGroupByField(name);                
        }
        
        // get query condition
        List conditions = getChildSection("CONDITION-LIST", true);
 
        if( conditions != null)
        for( int i=0; i<conditions.size(); i++)
        { 
            CompositeMap item = (CompositeMap)conditions.get(i);
            String qs  = item.getString("QUERY_STATEMENT");
            String lop = i==0? null: item.getString("LOGICAL_OPERATOR", "and");
 
            StringBuffer buf = new StringBuffer();
            String where_clause;
 
            if( qs != null) where_clause = qs;
            else {   
                String id  = item.getString("CONDITION_ID");
                Object value = getQueryValue(item);
                if(value==null) continue;
                String param_name = getParamName(id);
                if(item.getString("OPERATOR")==null) continue;
                
                if(item.getString("LEFT_BRACKET")!=null) 
                    appendConditionPart(buf, item, "LEFT_BRACKET");
                
                appendConditionPart(buf, item, "QUERY_FIELD");
                appendConditionPart(buf, item, "OPERATOR");
//System.out.println("1:param :"+param_name);                   
                if(param_name!=null) buf.append(" ${@" + param_name + "}");
 
                if(item.getString("RIGHT_BRACKET")!=null) 
                    appendConditionPart(buf, item, "RIGHT_BRACKET");
                
                where_clause = buf.toString();
    
                //where_clause = item.getString("QUERY_FIELD") + ' ' + item.getString("OPERATOR") + " ${@" + param_name + "}";
                params.put(param_name, value);
                
//System.out.println(v_order+":"+buf.toString());
            }
//System.out.println("lop is :"+lop);   
        
            if( lop != null) 
            { 
                stmt.getWhereClause().addWhereClause(lop,where_clause);  
            }
            else  
                stmt.getWhereClause().addWhereClause(where_clause); 

            //where_clause=where_clause+(" R)R ");
//System.out.println("where:"+where_clause);            
        }
 
        // order by
        List order_by = getChildSection("ORDER-LIST", true);
        if( order_by != null)
        for( int i=0; i<order_by.size(); i++)
        {
            CompositeMap item = (CompositeMap)order_by.get(i);  
            stmt.addOrderByField(item.getString("FIELD_NAME"), item.getString("SORT_TYPE"));    
        } 
//System.out.println(stmt.getSQL());        
        return stmt.getSQL();
                
    }
    
    public void createTableColumns( CompositeMap view ){
        List fields = getChildSection("FIELD-LIST", false);     
        for( int i=0; i<fields.size(); i++){
            CompositeMap item = (CompositeMap)fields.get(i);
            CompositeMap column = new CompositeMap(10);
            column.setName("column");
            String alias = "F" + i;

            column.put("Name", alias);
            column.put("dataField", "@" + alias);
            column.put("Prompt", item.getString("FIELD_TITLE", alias));
            column.put("GroupType", item.getString("GROUP_TYPE"));
            column.put("GroupLevel", item.getString("GROUP_LEVEL"));
            
            String data_type = item.getString("DATA_TYPE");
            if("VARCHAR2".equals(data_type))
                column.put("DataType", "2");
            else if("DATE".equals(data_type))
                column.put("DataType", "5");
                
            
            view.addChild(column);          
        }
//      System.out.println(view.toXML());
        
    }
    
    public boolean isGroupByQuery(){
        return "1".equals(getString("SQL_TYPE"));
    }

}
