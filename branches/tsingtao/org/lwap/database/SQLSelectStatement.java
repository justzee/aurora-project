/*
 * SQLSelectStatement.java
 *
 * Created on 2002年8月8日, 下午8:18
 */

package org.lwap.database;

import java.util.LinkedList;

/**
 *
 * @author  zhoufan
 */
public class SQLSelectStatement extends SQLStatement {
    
    LinkedList     field_list = new LinkedList();
    LinkedList	   order_list = new LinkedList();
    LinkedList	   group_list = new LinkedList();

    
    /** Creates a new instance of SQLSelectStatement */
    public SQLSelectStatement( String schema_name, String object_name) {
        super( schema_name, object_name);
    }
    
    public SQLSelectStatement( String object_name){
        super(  object_name);
    }
    
    public void addField( String fld_name, String fld_alias){
    	SQLSelectField fld = new SQLSelectField();
    	fld.field_name = fld_name;
    	fld.field_alias = fld_alias;
    	field_list.add(fld);
    }

    public void addField( String fld_name ){
    	addField(fld_name, null);
    }
    
    public void addGroupByField( String field_name){
    	group_list.add(field_name);
    }

    public void addOrderByField( String field_name ){
    	addOrderByField(field_name, null);
    }
    
    public void addOrderByField( String field_name, String sort_type ){
    	StringBuffer stmt = new StringBuffer(field_name);
    	if( sort_type != null) stmt.append(' ').append(sort_type);
    	order_list.add(stmt.toString());
    }
    
    
    public String getSQL() {
         int count =0;
         StringBuffer sql = new StringBuffer();
         sql.append("select ").append( SQLSelectField.getFieldList(field_list) );
         sql.append("\nfrom ").append(super.object_name).append('\n');
         sql.append( getWhereClause().getFullStatement() );
         if( group_list.size() > 0)
         	sql.append("\ngroup by ").append(SQLStatement.connectFields(group_list));
         if( order_list.size() > 0)
         	sql.append("\norder by ").append(SQLStatement.connectFields(order_list));
         
         return sql.toString();
    }    
    
    public static void main(String[] args) throws Exception {     
    	SQLSelectStatement sql = new SQLSelectStatement("tab");
    	sql.addField("count(tname)");
    	sql.addField("tabtype");
    	sql.getWhereClause().addWhereClause("tname like '%LBR%'");
    	sql.addGroupByField("tabtype");
    	sql.addOrderByField("tabtype", "desc");
    	sql.addOrderByField("2");    	
    	System.out.println(sql.getSQL());
    }
    
    
}
