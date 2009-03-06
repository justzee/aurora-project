/**
 * Created on: 2002-11-19 20:21:41
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;

public class ClassViewFactory implements ViewFactory {
	
	public static final String DEFAULT_NAMESPACE_URL = "http://mvc.lwap.org/default";

	ViewFactoryStore _store;
	String 		name_space_url = DEFAULT_NAMESPACE_URL;

	// name -> View mapping
	HashMap		view_map = new HashMap();     
	
    public void setViewFactoryStore(ViewFactoryStore store){
    	_store = store;
    }

    public ViewFactoryStore getViewFactoryStore(){
    	return _store;    
    }
    
    public Map getViewMap(){
    	return view_map;
    }
	
    public View createView(BuildSession session, String view_name, CompositeMap model, CompositeMap view ) throws ViewCreationException{
    	View the_view = (View) view_map.get(view_name);
    	if( the_view == null) throw new ViewCreationException("ClassViewBuilder:Can't find view "+view_name);
    	//the_view.build(session,model,view);
    	return the_view;
    }
    
    public  String getNamespaceURL(){
    	return name_space_url;
    }
    
    public void registerView( String name, View view_instance){
    	view_map.put(name,view_instance);
    }
    
    public void registerView( View view){
    	registerView(view.getViewName(), view);
    }
    
    public ClassViewFactory(){
    }
    
    public ClassViewFactory(String name_space){
    	this.name_space_url = name_space;
    }
/*    
    public void registerViews( Properties props){
    	Enume props.keys();
    }
 */   

}
