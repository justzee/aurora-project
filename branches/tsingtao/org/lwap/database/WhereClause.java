/*
 * WhereClause.java
 *
 * Created on 2002年8月8日, 下午9:19
 */

package org.lwap.database;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author  zhoufan
 */
public class WhereClause {
    
    
    public class WhereExpression{
        
        public String         logical_operator;
        public String         expression;
        public WhereClause sub_clause;
        
        
        public WhereExpression( String _logical_operator, String _expression){
            logical_operator = _logical_operator;
            expression = _expression;
        }
        
        public WhereExpression( String _logical_operator, WhereClause _sub_clause){
            logical_operator = _logical_operator;
            sub_clause = _sub_clause;
        }

        public WhereExpression( String _expression){
            this( SQLStatement.KEYWORD_AND, _expression);
        }
        
        public WhereExpression( WhereClause _sub_clause ){
            this( SQLStatement.KEYWORD_AND, _sub_clause);
        }
        
        public String getExpressionText(){
            if( expression != null) return expression;
            else if( sub_clause != null) return sub_clause.getWhereStatement();
            else return null;
        }
        
        public String getLogicalOperator(){
            if(logical_operator != null) return logical_operator ;
            else return SQLStatement.KEYWORD_AND;
        }
        
        
               
    };
    
     
     LinkedList    where_clause = new LinkedList();
     
     
    /** Creates a new instance of WhereClause */
    public WhereClause() {
    }
    

    
    public void addWhereClause( String clause ){
         where_clause.add( new WhereExpression(clause ));
     }

    public void addWhereClause( String logical_operator, String clause ){
         where_clause.add( new WhereExpression(logical_operator,clause ));
     }
     
     public void addWhereClause( String logical_operator, WhereClause clause ){
         where_clause.add(new WhereExpression(logical_operator, clause ));
     }
     
    public void addWhereClause( WhereClause clause){
         where_clause.add( new WhereExpression(clause ));
     }
    
     public String getWhereStatement(){
     	//if( where_clause.size()==0) return "";
         StringBuffer buf = new StringBuffer();
         int count = 0;
         Iterator it = where_clause.iterator();
         while( it.hasNext() ){
             WhereExpression exp = (WhereExpression)it.next();
             String text = exp.getExpressionText();
             String op = exp.getLogicalOperator();
             if( text == null) continue;
             if( count>0 ) buf.append(' ').append( op ).append(' ');
             buf.append('(').append(text).append(')');
             count++;
         }
         return buf.toString();
     }
     
     public String toString(){
         return getWhereStatement();
     }
     
     public String getFullStatement(){
         String stmt = getWhereStatement();
         if( stmt.length()>0) return "where " + stmt;
         else return "";
     }
     
     public static void main(String[] args) throws Exception {
         WhereClause join_conditions = new WhereClause();
         join_conditions.addWhereClause("e.job_id = j.job_id (+)");
         join_conditions.addWhereClause("e.unit_id = u.unit_id (+)");
         join_conditions.addWhereClause("e.employee_type = t.value (+)");
         
         WhereClause where = new WhereClause();
         where.addWhereClause( join_conditions);
         where.addWhereClause("and not", "employee_type=3");
         where.addWhereClause("or", "born_date >= ${parameter/@born_date}");
         
         System.out.println( where.getFullStatement() );
     }
     
    
}
