/**
 * Created on: 2003-4-14 18:20:40
 * Author:     zhoufan
 */
package org.lwap.metadata;

import java.util.Iterator;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;
import org.lwap.database.DatabaseQuery;
import org.lwap.database.SQLSelectStatement;
import org.lwap.database.WhereClause;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class MetaDataQuery implements MetadataProcessor {
	
	static String getElementName( String object, String schema ){
		return object + '_' + schema;
	}
	
	static String getElementName( CompositeMap metadata){
		String entity_name = metadata.getString("ENTITY_NAME");
		String schema_name = metadata.getString("SCHEMA_NAME");
		return getElementName(entity_name, schema_name);
	}

	public static void createQuery( BaseService service, CompositeMap metadata ) throws ServletException {
		
		String entity_name = metadata.getString("ENTITY_NAME");
		String schema_name = metadata.getString("SCHEMA_NAME");
		String element_name = getElementName(entity_name, schema_name);
		int single_element = ((Number)metadata.get("IS_SINGLE_ELEMENT")).intValue();
		
		SQLSelectStatement sql = new SQLSelectStatement(entity_name);

		CompositeMap fields = metadata.getChild("field-list");
		if (fields == null) return;
		Iterator it = fields.getChildIterator();
		if( it == null) throw new ServletException("MetaDataQuery: no field list in query config: "+metadata.toXML());
		
		while( it.hasNext()){
			CompositeMap item = (CompositeMap)it.next();
			sql.addField(item.getString("PHYSICAL_NAME"), item.getString("FIELD_NAME"));
		}
		
		fields = metadata.getChild("filter-list");
		if (fields != null){
			it = fields.getChildIterator();
			if( it != null){
				WhereClause where = sql.getWhereClause();
				while( it.hasNext()){
					CompositeMap item = (CompositeMap)it.next();
					where.addWhereClause(item.getString("EXPRESSION"));
				}
			}
		}
		
		DatabaseQuery query = DatabaseQuery.createQuery(sql.getSQL());

		CompositeMap model_config = service.getModelConfig();

		if( model_config == null) 
			model_config = service.getServiceConfig().createChild(BaseService.KEY_MODEL);
			

		CompositeMap existing_query = null;
		CompositeMap tmp = null;

		if( single_element == 1){
			tmp = model_config.getChildByAttrib( DatabaseQuery.KEY_ELEMENT_NAME, element_name);
			if(tmp != null) return;
		    query.setElementName(element_name);
			}
		else{
			tmp = model_config.getChildByAttrib( DatabaseQuery.KEY_TARGET, element_name);
			if(tmp != null) return;
			query.setTarget( element_name );
		}
		
		model_config.addChild(query.getObjectContext());
		
	}
	

	/**
	 * @see com.handchina.hrms.MetadataProcessor#processMetaData(BaseService, CompositeMap, CompositeMap)
	 */
	public void processMetaData(
		BaseService service,
		CompositeMap metadata,
		CompositeMap config)
		throws ServletException {
			
			createQuery(service,metadata);
	}

}
