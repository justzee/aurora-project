/*
 * SQLUpdateStatement.java
 *
 * Created on 2002年8月8日, 下午9:57
 */

package org.lwap.database;


import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author  zhoufan
 */
public class SQLUpdateStatement extends SQLStatement {
    
    public class UpdateField{
        
        public String fld_name;
        public String value;
        
        public UpdateField( String f, String v){
            fld_name = f;
            value = v;
        }
        
        public String getExpression(){
            return fld_name + '=' + value;
        }
    }
    
    LinkedList  update_fields = new LinkedList();
    
    /** Creates a new instance of SQLUpdateStatement */
    public SQLUpdateStatement( String schema_name, String object_name) {
        super( schema_name, object_name);
    }
    
    public SQLUpdateStatement( String object_name){
        super(  object_name);
    }
    
    public void addUpdateField( String fld_name, String value ){
        update_fields.add( new UpdateField( fld_name, value));
    }
    
     public  String getSQL(){
         int count =0;
         StringBuffer sql = new StringBuffer();
         sql.append("update ").append( getObjectName()).append(" set ");
         Iterator it = update_fields.iterator();
         while( it.hasNext()){
             if( count>0 ) sql.append(", ");
             sql.append( ((UpdateField)it.next()).getExpression() );
             count++;
         }
         sql.append(' ').append( getWhereClause().getFullStatement() );
         return sql.toString();
     }
     
     public static void main(String[] args) throws Exception {     
         SQLUpdateStatement stmt = new SQLUpdateStatement("HR_LBR_EMPLOYEE");
         stmt.addUpdateField("EMPLOYEE_NAME", "${parameter/@EMPLOYEE_NAME}");
         stmt.addUpdateField("EMPLOYEE_CODE", "${parameter/@EMPLOYEE_CODE}");         
         stmt.getWhereClause().addWhereClause("EMPLOYEE_ID=${parameter/@EMPLOYEE_ID}");
         System.out.println(stmt.getSQL());
     }
    
}
