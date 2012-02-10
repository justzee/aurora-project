/*
 * JspViewBuilder.java
 *
 * Created on 2002年1月13日, 下午3:39
 */

package org.lwap.mvc.servlet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.javautil.file.FileUtil;
import org.javautil.file.WildcardFilter;
import org.lwap.mvc.BuildSession;
import org.lwap.mvc.View;
import org.lwap.mvc.ViewCreationException;
import org.lwap.mvc.ViewFactory;
import org.lwap.mvc.ViewFactoryStore;

import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;

/**
 *
 * @author  Administrator
 * @version 
 */
public class JspViewFactory implements ViewFactory {
    
   ServletContext 		context;
   String 				jsp_extension  = null;   
   CompositeLoader		composite_loader;
   String				context_path;
   
   Map					controls;
   
   public static final String NAMESPACE_URL = "http://mvc.lwap.org/builder/jsp";
   public static final String JSP_EXTENSION = ".jsp";  
   public static final String KEY_SESSION = "session";
   public static final String KEY_MODEL = "model";
   public static final String KEY_VIEW = "view";   
   
   public static CompositeMap getModel( HttpServletRequest request){
       return (CompositeMap)request.getAttribute(KEY_MODEL);
   }
   
   public static CompositeMap getView( HttpServletRequest request){
       return (CompositeMap)request.getAttribute(KEY_VIEW);
   }
   
   public static BuildSession getBuildSession( HttpServletRequest request){
       return (BuildSession)request.getAttribute(KEY_SESSION);
   }
   
   public static CompositeMap createView( String view_name){
       CompositeMap view = new CompositeMap(20);
       view.setName(view_name);
       view.setNameSpace("jsp",NAMESPACE_URL);
       return view;
   }
   
	ViewFactoryStore _store;
	
    public void setViewFactoryStore(ViewFactoryStore store){
    	_store = store;
    }

    public ViewFactoryStore getViewFactoryStore(){
    	return _store;    
    }


   public String getJspName(String name){
       if( jsp_extension == null) return name+JSP_EXTENSION;
       else return name + jsp_extension;
   }
   
   public void setJspExtension(String ext){
       jsp_extension = ext;
   }
   
   public String getJspExtension(){
   		return jsp_extension == null? JSP_EXTENSION: jsp_extension;
   }

    /** Creates new JspViewFactory */
    public JspViewFactory(ServletContext ct, String context_path) {
      /*
        try{
       System.out.println("********* Initializing templates path [" + context_path + "]");
       System.out.println("Context name:" + ct.getResource(".").toString() );
       System.out.println("Physical path:" + ct.getRealPath("/"));
        }catch(Exception ex){
            
        }
       */
       setServletContext(ct);
       setContextPath(context_path);
       loadControls();
    }
    
    public JspViewFactory(){
    }
    

    public void setContextPath( String path){
    	if( path == null) return;
    	if( path.charAt(path.length()-1) != '/')
    		path += '/';
    	this.context_path = path;
    }
    
    public String geContextPath(){
    	return context_path;
    }
    

    
    public void setServletContext( ServletContext ct){
        context = ct;
    }

    public View createView(BuildSession _session, String view_name, CompositeMap model, CompositeMap view) 
    throws ViewCreationException {

        ServletBuildSession session = (ServletBuildSession)_session;
        String path = this.context_path;
        
        if( view_name == null) view_name = view.getName();
        if( controls.containsKey(view_name)){
        	String sub_context = (String)controls.get(view_name) + '/';
        	path += sub_context;
        	view_name =  sub_context + view_name;
        }
        _session.setCurrentContext(path);
        
        HttpServletRequest request = session.getRequest();
        request.setAttribute(KEY_SESSION, session);
        request.setAttribute(KEY_MODEL, model);
        request.setAttribute(KEY_VIEW, view);
        RequestDispatcher disp = context.getRequestDispatcher( '/'+getJspName(view_name));
        
        if( disp == null) {
            throw new ViewCreationException("No jsp found in " + context.getServletContextName() + ":"+view_name);
        }
        
        return new JspView( disp, view_name);
        
    }

    public  String getNamespaceURL() {
        return NAMESPACE_URL;  
    }
    
    
	public void loadControls(File dir){
		String dir_name = dir.getName();
		//System.out.println("Searching "+dir_name);
		WildcardFilter filter = new WildcardFilter("*" + getJspExtension() );
		File[] jsps = dir.listFiles(filter);
		if( jsps != null)
			for(int i=0; i<jsps.length; i++){
				String control_name = FileUtil.getNameNoExt(jsps[i]);
				this.controls.put(control_name, dir_name);
//				System.out.println("  "+control_name+"@"+dir_name);
			}
	}

	public void loadControls(){
		if( controls == null) controls = new HashMap();		
		File path = new File(this.context.getRealPath("/"));
        File[] files = path.listFiles();
		if( files != null)
			for(int i=0; i<files.length; i++){
				File file = files[i];
				if(file.isDirectory()) loadControls( file);
			}
/*
		ViewConfig config = _store.getViewConfig();
		config.getResourcePath("/")
*/		
	}	

    
}
