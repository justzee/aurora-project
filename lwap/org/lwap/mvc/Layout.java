/**
 * Created on: 2002-11-25 19:24:19
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import uncertain.util.AdaptiveTagParser;
import org.lwap.mvc.servlet.ServletBuildSession;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;

/**
 *  Layout views in tabular format or by template
 *  <code>
 *      <std:layout Type="template|tabular"
 *            dataModel="model_name"
 *           [Template="template_name" |
 *            HandleClass="cls_name" CellWidth="width" CellHeight="height" CellStyle="class_name" Columns="column_count"
 *           ]>
 *      </std:layout>
 *  </code>
 */
public class Layout implements View {
	
	public static final String KEY_TYPE = "Type";
	public static final String KEY_HANDLE_CLASS = "HandleClass";
	public static final String KEY_CELL_WIDTH = "CellWidth";
	public static final String KEY_CELL_HEIGHT = "CellHeight";
	public static final String KEY_CELL_STYLE = "CellStyle";
	public static final String KEY_CELL_ALIGN = "CellAlign";
    public static final String KEY_CELL_VALIGN = "CellVAlign";
	public static final String KEY_COLUMNS = "Columns";				

	public static final String KEY_TEMPLATE = "Template";
	public static final String KEY_NAME = "Name";

	public static final String VIEW_SECTION = "view-section";
	public static final String TYPE_VALUE_TEMPLATE = "template";	
	public static final String TYPE_VALUE_TABULAR = "tabular";	
	
	public void buildTemplate(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
		//open template stream
		String tpl = view.getString(KEY_TEMPLATE);
		if( tpl == null) throw new IllegalArgumentException("Must set 'Template' property for template layout: "+view.toXML());
		File tpl_file = 	new File( ((ServletBuildSession)session).getContext().getRealPath(tpl));
		if( !tpl_file.exists()) {
			tpl_file = 	session.getViewFactoryStore().getViewConfig().getResourcePath(tpl);
			if( tpl_file == null  ) throw new ViewCreationException("Can't load template "+tpl);
		}

		FileReader reader = null;
		try{
			reader = new FileReader(tpl_file);
		} catch(FileNotFoundException ex){
			throw new ViewCreationException("Can't load template "+tpl_file.getPath()+", exception: "+ex.getMessage());
		}
		
		CompositeMap layout_model = DataBindingConvention.getDataModel(model,view);
		
		try{
			// read template into buffer
			StringBuffer buf = new StringBuffer();
			int ch;
			while( (ch =  reader.read()) != -1){
					buf.append((char)ch);
			}		
			
	
			// build section name -> collection of view HashMap
			Iterator it = view.getChildIterator();
			HashMap		sections = new HashMap(40);
            if(it!=null)
    			while(it.hasNext()){
    				CompositeMap section = (CompositeMap) it.next();
    			    if( VIEW_SECTION.equals(section.getName() ) ){
    			    	String name = section.getString(KEY_NAME);
    			    	Collection childs = section.getChilds();
    			    	if( name != null && childs != null) sections.put(name,childs);
    			    } else if (section.containsKey(KEY_NAME)){
    			    	String name = section.getString(KEY_NAME);			    	
    			    	sections.put(name, section);
    			    }
    			}		
			
			// write template contents
			Writer out = session.getWriter();
			LinkedList contents = TemplateContent.buildTemplateContent(buf.toString());
			it = contents.iterator();
			while(it.hasNext()){
				TemplateContent content = (TemplateContent)it.next();
				if( content.type == TemplateContent.TYPE_TEMPLATE_FRAGMENT)
					out.write(content.content);
                else if( content.type == TemplateContent.TYPE_SERVICE_INVOKE ){
                    out.flush();
                    ViewFactoryStore store = session.getViewFactoryStore();
                    CompositeMap view_config = store.createView("service");
                    view_config.put("Name", content.content);
                    session.buildView(model, view_config);
                }
				else{
					out.flush();
                    //String tag = content.content;
					Object obj = sections.get(content.content);                    
					if( obj == null){
                        //System.out.println(sections);
                        //System.out.println("[Layout] Can't find tag '" +content.content+"' for file " + tpl_file.getPath());
                    }

					if( obj instanceof CompositeMap){
						session.buildView(layout_model, (CompositeMap)obj);
					}else if( obj instanceof Collection){
						Collection views = (Collection) obj;
						session.applyViews(layout_model,views);
					}
				}
						
			}
	
		}catch(IOException ex){
			throw new ViewCreationException(ex);
		} finally {
			try{
				reader.close();
			}catch(IOException ex){
			}
		}

	}
	
	protected TabularLayout.LayoutHandle createDefaultHandle( CompositeMap view){
		ViewBasedLayoutHandle handle = new ViewBasedLayoutHandle();
		handle.initialize(view);
		return handle;
	}
	
	protected void buildTabular(
			BuildSession session,
			CompositeMap model,
			CompositeMap view,
			TabularLayout.LayoutHandle handle)
			throws ViewCreationException {
		int col_count = view.getInt(KEY_COLUMNS, TabularLayout.COLUMN_COUNT_UNLIMITED);
		TabularLayout layout = new TabularLayout(col_count,handle);
		layout.layout(session,model,view);	    
	}
	
	public void buildTabular(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
			
		TabularLayout.LayoutHandle handle;
		
		
		String cls_name = view.getString(KEY_HANDLE_CLASS);
		if( cls_name != null){
			try{
				Class handle_cls = Class.forName(cls_name);
				handle = (TabularLayout.LayoutHandle)DynamicObject.cast(view,handle_cls);				
			}catch(Throwable thr){
				throw new ViewCreationException(thr);				
			}
		} else{
			handle = createDefaultHandle(view);
		}

		buildTabular(session,model,view,handle);
	}

		
	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
		String type = view.getString(KEY_TYPE, TYPE_VALUE_TEMPLATE);
		if( TYPE_VALUE_TEMPLATE.equals( type) || view.get(KEY_TEMPLATE)!=null){
            buildTemplate(session,model,view);
		}
		else if( TYPE_VALUE_TABULAR.equals(type)){
            buildTabular(session,model,view);
		}
		else{
		    buildTabular(session,model,view);
		}
	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return "layout";
	}

}
