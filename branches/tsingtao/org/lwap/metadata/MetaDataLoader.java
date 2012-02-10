/**
 * Created on: 2003-4-14 17:19:47
 * Author:     zhoufan
 */
package org.lwap.metadata;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lwap.application.BaseService;
import org.lwap.application.Service;
import org.lwap.application.ServiceParticipant;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

/**
 * load meta data before a service executes
 */
public class MetaDataLoader implements ServiceParticipant {
	
	public static final String KEY_SERVICE_METADATA = "service-metadata";
	public static final String KEY_OBJECT = "object";
	public static final String KEY_SCHEMA = "schema";
	public static final String KEY_CLASS = "meta-class";
	
	public static final String KEY_FIELD_NAME = "FIELD_NAME";
	public static final String KEY_GROUP_TITLE = "GROUP_TITLE";
	public static final String KEY_LOOKUP_SCHEMA = "LOOKUP_SCHEMA";
	
	public static final String FIELD_LIST = "field-list";
	public static final String FILTER_LIST = "filter-list";	
	
	static CompositeMap getMetaData( BaseService service, String query_name, CompositeMap root ) throws ServletException {

		CompositeMap    query = null;
		CompositeLoader loader = ((WebApplication)service.getApplication()).getCompositeLoader();
		try{
			query  = loader.loadByFile(query_name);
		} catch( Throwable thr){
			throw new ServletException(thr);
		}

		service.databaseAccess(query,root,root);
		
		CompositeMap meta = root.getChild("metadata");
		
		if( meta == null) return null;
		CompositeMap field_list = meta.getChild(FIELD_LIST);
		
		// add schema properties
		CompositeMap sp = root.getChild("schema-properties");
		if( sp != null){
			Iterator it = sp.getChildIterator();
			if( it != null)
			while( it.hasNext()){
				CompositeMap item = (CompositeMap) it.next();
				meta.putString(item.getString("PROPERTY_NAME"), item.getString("PROPERTY_VALUE"));
			}
		}
		// add field properties
		CompositeMap fp = root.getChild("field-properties");
		Collection   fields = field_list.getChilds();
		if( fp != null && fields != null){
			Iterator it = fp.getChildIterator();
			if( it != null)
			while( it.hasNext()){
				CompositeMap item = (CompositeMap) it.next();
				Object sf_id = item.get("SCHEMA_FIELD_ID");
				CompositeMap field = field_list.getChildByAttrib("SF_RECORD_ID", sf_id);
				if( field != null){
					Iterator fit = item.getChildIterator();
					if( fit != null)
					while( fit.hasNext()){
						CompositeMap fpitem = (CompositeMap) fit.next();
						field.put(fpitem.getString("PROPERTY_NAME"), fpitem.getString("PROPERTY_VALUE"));
					}
				}
			}
		}
		
		
		return meta;
	}

	public static CompositeMap getMetaData( BaseService service, String object_name, String schema_name ) throws ServletException {
		
		CompositeMap    root = new CompositeMap("meta-data");
		root.put("object_name", object_name);
		root.put("schema_name", schema_name);
		
		return getMetaData( service, "metadata-by-name.data", root );
		
	}
	
	public static CompositeMap getMetaData( BaseService service, Object schema_id ) throws ServletException {
		
		CompositeMap    root = new CompositeMap("meta-data");
		root.put("schema_id", schema_id);
		
		CompositeMap meta =  getMetaData(service, "metadata-by-id.data", root );
		return meta;
		
	}
	
	public static CompositeMap getCompositeMap( WebApplication app, CompositeMap config, String attrib_name ){
		if( config == null) return null;
		String text = config.getString(attrib_name);
		if( text == null) return null;
		CompositeLoader loader = app.getCompositeLoader();
		try{
			return loader.loadFromString(text);
		}catch(Throwable thr){
			return null;
		}
	}
	

	/**
	 * @see org.lwap.application.ServiceParticipant#init(CompositeMap)
	 */
	public void init(CompositeMap params) {
	}
	
	static String getMetaParameter( CompositeMap item, String key) throws ServletException {
		String str = item.getString(key);
		if (str == null) throw new ServletException("MetaDataLoader: required parameter missing in <service-metadata> section: " + key);
		return str;
	}

	/**
	 * @see org.lwap.application.ServiceParticipant#service(HttpServletRequest, HttpServletResponse, Service)
	 */
	
	void callMetaProcessor(  BaseService svc, CompositeMap item, CompositeMap metadata )
				throws ServletException{
			WebApplication app = (WebApplication) svc.getApplication();					
			String cls_name    = getMetaParameter(item,KEY_CLASS);
			MetadataProcessor mp = (MetadataProcessor)app.getPooledObject(cls_name);
			if( mp == null) throw new ServletException("MetaDataLoader: can't create instance of " + cls_name);
			mp.processMetaData(svc,metadata,item);					
	}
	
	public int service(
		HttpServletRequest request,
		HttpServletResponse response,
		Service service)
		throws IOException, ServletException {
		
		BaseService svc = (BaseService) service;
		WebApplication app = (WebApplication) svc.getApplication();

		CompositeMap meta_list = svc.getServiceConfigSection(KEY_SERVICE_METADATA);
		if( meta_list == null) return ServiceParticipant.SERVICE_CONTINUE;
		
		Iterator it = meta_list.getChildIterator();
		if( it == null) return ServiceParticipant.SERVICE_CONTINUE;
		
		svc.setHttpObject(request,response);
		
		while( it.hasNext()){
			CompositeMap item = (CompositeMap) it.next();

			String obj = getMetaParameter(item, KEY_OBJECT);
			String schema = getMetaParameter(item, KEY_SCHEMA);
			String cls = item.getString(KEY_CLASS);
			
			CompositeMap metadata = getMetaData(svc, obj,schema);
			if( metadata == null) throw new ServletException("MetaDataLoader: can't load meta data " + obj + '.' + schema);
			
			if( cls != null) callMetaProcessor(svc,item,metadata );
			else{
				Iterator cit = item.getChildIterator();
				if( cit == null) throw new ServletException("MetaDataLoader: no processor class specified for meta data");
				while( cit.hasNext()){
					CompositeMap citem = (CompositeMap) cit.next();
					callMetaProcessor( svc, citem, metadata);
				}
			}
/*				
			MetadataProcessor mp = (MetadataProcessor)app.getPooledObject(cls);
			if( mp == null) throw new ServletException("MetaDataLoader: can't create instance of " + cls);
			
			mp.processMetaData(svc,metadata,item);
*/			
		}
		
		return ServiceParticipant.SERVICE_CONTINUE;
			
	}

}
