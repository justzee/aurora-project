/**
 * Created on: 2003-4-14 19:03:56
 * Author:     zhoufan
 */
package org.lwap.metadata;


import java.util.Iterator;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;
import org.lwap.application.WebApplication;
import org.lwap.mvc.DataBindingConvention;
import org.lwap.mvc.ViewFactoryStore;
import org.lwap.ui.web.Form;

import uncertain.composite.CompositeMap;
import uncertain.composite.IterationHandle;
import uncertain.composite.transform.GroupTransformer;


/**
 * 
 */
public class MetaDataForm  implements MetadataProcessor {
    
	public static class FormFinder implements IterationHandle {
		
		public CompositeMap form;
		
		public int process( CompositeMap map){
			String name = map.getName();
			if( name != null)
				if( name.equals("form")){
					form = map;
					return IterationHandle.IT_BREAK;
				}
			return IterationHandle.IT_CONTINUE;	
		}
	};
	
	public static final String KEY_FORM_CONFIG = "FORM_CONFIG";
/*	
	CompositeMap getGroupList( CompositeMap metadata ){
		
	}
*/	

	static CompositeMap createSection( WebApplication  app, String model, String prompt ){
		CompositeMap fs = app.getViewBuilderStore().createView("form-section");
		if( model != null) fs.putString("dataModel", model + "/record" );
		if( prompt !=  null) fs.putString("Prompt", prompt);
		fs.putString("PromptWidth", "30%");
		fs.putString("InputWidth", "70%");
		return fs;
	}
	
	static void putDefault( CompositeMap fld, String key, String value ){
		if( !fld.containsKey(key)) fld.put(key, value);
	}
	
	
	public static void createForm( BaseService service, CompositeMap metadata, CompositeMap config )
	throws ServletException {

		WebApplication  app = (WebApplication)service.getApplication();
		ViewFactoryStore store = app.getViewBuilderStore();
		CompositeMap	svc_config = service.getServiceConfig();

		CompositeMap form_config 	= MetaDataLoader.getCompositeMap(app,metadata,KEY_FORM_CONFIG);
		if( form_config == null){ 
		    FormFinder ff = new FormFinder();
		    svc_config.iterate(ff,true);
		    form_config = ff.form;
		    //form_config = (CompositeMap)svc_config.getObject("view/form");
		    
		}
		if( form_config == null) form_config = app.getViewBuilderStore().createView("form");
		
		putDefault(form_config,"dataModel", MetaDataQuery.getElementName(metadata));
		putDefault(form_config,"Entity", metadata.getString("OBJECT_NAME"));
		
		boolean read_only = false;
		String n = metadata.getString("READ_ONLY");
		if( n != null) read_only = n.equals("1");
		
		if(read_only) form_config.putBoolean(Form.KEY_SHOW_COMMIT_BUTTON,false);

		CompositeMap field_list = metadata.getChild(MetaDataLoader.FIELD_LIST);
		if( field_list == null) return;
		
		CompositeMap tf = GroupTransformer.createGroupTransform(MetaDataLoader.KEY_GROUP_TITLE, "group");
		GroupTransformer.addGroupField(tf,MetaDataLoader.KEY_GROUP_TITLE,false);
		GroupTransformer.getInstance().transform(field_list,tf);
		
		Iterator it = field_list.getChildIterator();
		if( it == null) return;
		
		while( it.hasNext()){
			CompositeMap group = (CompositeMap) it.next();
			Iterator items = group.getChildIterator();
			if( items == null) continue;
			CompositeMap section = createSection(app,null,group.getString(MetaDataLoader.KEY_GROUP_TITLE));
			form_config.addChild(section);

			while( items.hasNext()){
			
				CompositeMap item = (CompositeMap)items.next();
				Object 		 schema_id = item.get(MetaDataLoader.KEY_LOOKUP_SCHEMA);
				CompositeMap fld = MetaDataLoader.getCompositeMap(app,item,"CONTROL_CONFIG");
				
				if( fld == null){
					fld = store.createView("input");
					String control_type = item.getString("CONTROL_TYPE");
					if( control_type != null)
						fld.putString("Type", control_type);
					else{
						if(schema_id == null){
							String data_type = item.getString("APP_DATA_TYPE");
							if( data_type.equals("java.lang.Boolean"))
								fld.putString("Type", "checkbox");
							else if (data_type.equals("java.sql.Date"))
								fld.putString("Type", "datepicker");
							else							
								fld.putString("Type", read_only?"textlabel":"textedit");
						}
						else{
							fld.putString("Type", "select");		
						}
					}				
										
				}
				
				if( schema_id != null){
					
					CompositeMap lookup_meta = MetaDataLoader.getMetaData(service,schema_id);
					if( lookup_meta == null) throw new ServletException("MetaDataForm: can't load lookup schema for field " + item.getString(MetaDataLoader.KEY_FIELD_NAME));
					MetaDataQuery.createQuery(service,lookup_meta);
	
					putDefault( fld,DataBindingConvention.KEY_DATASOURCE,"/model/" + MetaDataQuery.getElementName(lookup_meta));
					putDefault( fld,DataBindingConvention.KEY_VALUE_FIELD, '@'   + item.getString("VALUE_FIELD")) ;
					putDefault( fld,DataBindingConvention.KEY_DISPLAY_FIELD, '@' + item.getString("DISPLAY_FIELD") );
				}	
					
				String name = item.getString( MetaDataLoader.KEY_FIELD_NAME);
				fld.putString("Name", name);
				putDefault( fld,DataBindingConvention.KEY_DATAFIELD, '@' + name );
				fld.putString("DataType", item.getString("APP_DATA_TYPE"));
				boolean nullable = "1".equals(item.getString("NULLABLE"));
				fld.putBoolean("Nullable", nullable );
				fld.putString("DefaultValue", item.getString("DEFAULT_VALUE"));
				if( read_only) fld.putBoolean("ReadOnly", true);
				
				section.addChild(fld);
			}
			
		}
		
		if( form_config.getParent() == null){ 
			CompositeMap view_config = service.getViewConfig();
			if( view_config == null) view_config = svc_config.createChild(BaseService.KEY_VIEW);
			view_config.addChild(form_config);
		}
		
		CompositeMap actions = service.getActionConfig();
		
	}
	
	
	public static void createAction(		BaseService service,
		CompositeMap metadata,
		CompositeMap config)
		throws ServletException {
		}

	public void processMetaData(
		BaseService service,
		CompositeMap metadata,
		CompositeMap config)
		throws ServletException {
		
		String action_type = metadata.getString("ACTION_TYPE","update");
		boolean is_update = action_type.equalsIgnoreCase("update");
		
		if( is_update){
			//System.out.println("is update");
			MetaDataQuery.createQuery(service,metadata);
			MetaDataUpdate.createUpdate(service,metadata,config);
		}	
		createForm(service,metadata,config);

	}
		
	

}