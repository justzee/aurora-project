/*
 * ViewBuilderStore.java
 *
 * Created on 2002年8月2日, 下午14:32
 */

package org.lwap.mvc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uncertain.composite.CompositeMap;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ViewFactoryStore {
	
	public static final String VIEW_IMPL_SECTION = "view-implementation";
	public static final String KEY_VIEW_NAME = "name";
	public static final String KEY_FACTORY_URL = "factory-url";	    
	public static final String KEY_MAPPED_VIEW_NAME = "view-name";
    
    
    HashMap         builder_map;
    ViewFactory     default_builder;
    ViewConfig		view_config;
	
	// view_name -> ViewFactory    
    Map				view_factory_mapping; 

    /** Creates new TransformerManager */
    public ViewFactoryStore() {
        builder_map = new HashMap();
        view_factory_mapping = new HashMap();
    }
    
    public ViewFactory getViewFactory( String url){
        return (ViewFactory)builder_map.get(url);
    }
    
    public void setDefaultFactory(ViewFactory vb){
        default_builder = vb;
    }
    
    
    public ViewFactory getDefaultFactory(){
        return default_builder;
    }

    
    
    public void registerViewFactory( ViewFactory builder){
        registerViewFactory( builder.getNamespaceURL(), builder);
    }
    
    public void registerViewFactory( String url, ViewFactory builder){
        builder_map.put(url, builder);
        builder.setViewFactoryStore(this);
    }
    
    public void setURLMapping( String url, String mapped_to_url){
        Object obj = builder_map.get(mapped_to_url);
        if( obj != null)
            builder_map.put( url, obj);
    }
    
    public CompositeMap createView(String view_name){
    	String url = (String)view_factory_mapping.get(view_name);
    	if( url == null) return null;
    	CompositeMap view = new CompositeMap(20);
    	view.setNameSpaceURI(url);
    	view.setName(view_name);
    	return view;
    }
    
    public View createViewInstance( BuildSession session, String view_name, CompositeMap model, CompositeMap view)
    	throws ViewCreationException
    {
    	String url = (String)view_factory_mapping.get(view_name);
    	if( url == null) return null;
    	return getViewFactory(url).createView(session,view_name,model,view);
    }
    
    
    public void setViewImplementation( String view_name, String namespace_url){
        this.view_factory_mapping.put(view_name,namespace_url);
    }
    
    public void setViewImplementation( String view_name, ViewFactory fact){
    	setViewImplementation(view_name,fact.getNamespaceURL());
    }
 
 

	/**
	 * Returns the view_config.
	 * @return ViewConfig
	 */
	public ViewConfig getViewConfig() {
		return view_config;
	}

	/**
	 * Sets the view_config.
	 * @param view_config The view_config to set
	 */
	public void setViewConfig(ViewConfig view_config) {
		this.view_config = view_config;
		loadViewImplementation( view_config.getConfig().getChild(VIEW_IMPL_SECTION));
	}
	
	public void loadViewImplementation( CompositeMap config){
		if( config == null) return;
		Iterator it = config.getChildIterator();
		while( it.hasNext()){
			CompositeMap conf = (CompositeMap)it.next();
			setViewImplementation( conf.getString(KEY_VIEW_NAME), conf.getString(KEY_FACTORY_URL));
		}
	}
	

}
