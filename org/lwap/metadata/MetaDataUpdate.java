/**
 * Created on: 2003-4-14 18:27:26
 * Author:     zhoufan
 */
package org.lwap.metadata;


import java.util.Iterator;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;
import org.lwap.database.DatabaseUpdate;
import org.lwap.database.SQLUpdateStatement;
import org.lwap.database.WhereClause;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class MetaDataUpdate implements MetadataProcessor {
	
	public static final String KEY_LOCATION = "location";
	
	public static void createUpdate( BaseService service, CompositeMap metadata, CompositeMap config ) 
	throws ServletException {
		
		String entity_name = metadata.getString("ENTITY_NAME");
		
		SQLUpdateStatement sql = new SQLUpdateStatement(entity_name);

		CompositeMap fields = metadata.getChild("field-list");
		if (fields == null) return;
		Iterator it = fields.getChildIterator();
		if( it == null) return;
		
		while( it.hasNext()){
			CompositeMap item = (CompositeMap)it.next();
			Number for_update = (Number)item.get("FOR_UPDATE");
			if( for_update != null)
				if( for_update.intValue() == 1){
					String fld_name = item.getString("FIELD_NAME");
					sql.addUpdateField(fld_name, "${@" + fld_name + "}" );
				}
		}
		
		fields = metadata.getChild("filter-list");
		if (fields != null) {
			it = fields.getChildIterator();
			if( it != null){
				WhereClause where = sql.getWhereClause();
				while( it.hasNext()){
					CompositeMap item = (CompositeMap)it.next();
					where.addWhereClause(item.getString("EXPRESSION"));
				}
			}
		}
		
		
		DatabaseUpdate upd = DatabaseUpdate.createUpdate(sql.getSQL());
		
		String location = BaseService.KEY_ACTION;
		if( config != null)
			location = config.getString(KEY_LOCATION,BaseService.KEY_ACTION);
		
		CompositeMap action = service.getServiceConfigSection(location);
		if( action == null) action = service.getServiceConfig().createChild(location);
		action.addChild(upd.getObjectContext());
		
	}
	

	/**
	 * @see com.handchina.hrms.MetadataProcessor#processMetaData(BaseService, CompositeMap, CompositeMap)
	 */
	public void processMetaData(
		BaseService service,
		CompositeMap metadata,
		CompositeMap config)
		throws ServletException {
			
			createUpdate(service,metadata, config);
	}
	

}
