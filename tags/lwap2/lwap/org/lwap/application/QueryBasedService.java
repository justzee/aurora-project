/**
 * Created on: 2002-11-17 15:59:18
 * Author:     zhoufan
 */
package org.lwap.application;


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.lwap.database.DatabaseQuery;
import org.lwap.database.WhereClause;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.ui.UIAttribute;
import org.lwap.ui.web.Form;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/** Notice: use lower case of "select" and "from" in query-statement!
 */

public class QueryBasedService extends FormBasedService {

	public static final String KEY_AUTO_QUERY = "auto-query";
	public static final String KEY_QUERY_STATEMENT = "query-statement";
	public static final String KEY_OPERATOR = "Operator";
	public static final String KEY_WHERE_TAG = "#WHERE_CLAUSE#";
	public static final String KEY_DEFAULT_WHERE = "DefaultWhere";
    public static final String KEY_QUERY_WHERE = "QueryWhere";
    public static final String KEY_AUTO_COUNT = "AutoCount";    
    public static final String KEY_COUNT_FIELD = "CountField";
    
	/** create a org.lwap.ui.web.Form object from view config */
	protected Form createForm() throws ServletException {
		the_form = super.createForm();
		the_form.setParamSourcePrefix("/parameter/@");
		the_form.createParameter("pagesize",Long.class,false,"30");
		the_form.createParameter("pagenum",Long.class,false,"1");

		String str = the_form.getString(Form.KEY_SUBMIT_PROMPT);
		if (str == null) the_form.putString(Form.KEY_SUBMIT_PROMPT, getLocalizedString("prompt.query"));
		
		return the_form;
	}

	protected WhereClause getWhereClause( Form frm, CompositeMap parameters ){
		WhereClause where = new WhereClause();
		Map params = frm.getInputFields();
		Iterator it = params.values().iterator();
		while( it.hasNext()){
			CompositeMap input_fld = (CompositeMap)it.next();
			String name = UIAttribute.getName(input_fld);
			if( parameters.containsKey(name) ){
				StringBuffer expr = new StringBuffer();
                String queryWhere = input_fld.getString(KEY_QUERY_WHERE);
				String operator = input_fld.getString(KEY_OPERATOR);
				
                if (queryWhere != null)
                   where.addWhereClause(queryWhere);
                else if(operator != null){
					expr.append(name).append(' ').append(operator).append(" ${").append(input_fld.getString(DataBindingConvention.KEY_DATAFIELD) ).append('}');
					where.addWhereClause(expr.toString());
                }
			}
		}
		return where;
	}
	

	public void createModel() throws  IOException,ServletException{

		CompositeMap model_config = getModelConfig();
		boolean is_auto_query = super.getServiceConfig().getBoolean(KEY_AUTO_QUERY,false);
		
		if( isFormPost() || is_auto_query){

			Form form = createForm();
			form.initForm();
			boolean param_valid = form.parseParameter( super.getParameterSource(), getParameters());
			
			if( param_valid && model_config != null){
				CompositeMap query_statement = model_config.getChild(KEY_QUERY_STATEMENT);
				if(query_statement != null){
				
					// create query statement
					DatabaseQuery query = (DatabaseQuery)DynamicObject.cast( query_statement, DatabaseQuery.class);
					String sql = query.getSql();
					if( sql==null) throw new IllegalArgumentException("QueryBasedService:must specify 'Sql' property");
					String default_where = query.getObjectContext().getString(KEY_DEFAULT_WHERE);
					WhereClause where = getWhereClause( this.getForm(), this.getParameters());
					if( default_where != null) where.addWhereClause(default_where);
					StringBuffer buf = new StringBuffer(sql);
					int idx = buf.indexOf(KEY_WHERE_TAG);
					if( idx<0) throw new ServletException("QueryBasedService: #WHERE_CLAUSE# not set in query statement");
					buf.replace(idx,idx+KEY_WHERE_TAG.length(),where.getFullStatement());
					sql = buf.toString();
					query.setSql(sql);
					query.setAccessType(DatabaseQuery.QUERY);
					
					// create select count statement
					boolean auto_count = query.getBoolean(KEY_AUTO_COUNT, true);
					if( auto_count){
						String count_field = query.getString("CountField", "TOTAL_COUNT");
						StringBuffer count_sql = new StringBuffer();
                        count_sql.append("select count(*) as " + count_field + " from ( ").append(sql).append(")");
                        DatabaseQuery count_query = DatabaseQuery.createQuery(count_sql.toString());
                        count_query.setPageResultset(false);
                        count_query.setElementName("query-count");
                        model_config.addChild(count_query.getObjectContext());

                        /*
						int index_from = count_sql.indexOf("select");
						int index_to   = count_sql.indexOf("from")+1;
						if( index_from>=0 && index_to>7){
							count_sql.replace(index_from+7,index_to-1, " count(*) as "+count_field+" ");
							DatabaseQuery count_query = DatabaseQuery.createQuery(count_sql.toString());
							count_query.setPageResultset(false);
							count_query.setElementName("query-count");
							model_config.addChild(count_query.getObjectContext());
						}else
							application.getLogger().warning("can't create count statement from query sql. Hint: 'select' and 'from' must be lower case.\r\n Original sql:"+sql);
                            */
					}
					
                }
			}
		
		}
		
		
		//if( !isDump())
		 super.createModel();
	}

	public void doServiceFormPost() throws IOException, ServletException {
		super.doServiceFormPost();
		super.setViewOutput(true);
	}

/*
	public static void main(String[] args){
		String test = "select * from fields #WHERE_CLAUSE# order by name";
		StringBuffer buf = new StringBuffer(test);
		int idx = buf.indexOf(KEY_WHERE_TAG);
		buf.replace(idx,idx+KEY_WHERE_TAG.length(),"where a=b");
//		System.out.println(test.replaceAll(KEY_WHERE_TAG,"where a=b"));
		System.out.println(buf.toString());
		WhereClause w = new WhereClause();
		w.addWhereClause("T=1");
		w.addWhereClause("B=2");
		System.out.println(w.getFullStatement());
	}
*/
    
/*
*/


}
