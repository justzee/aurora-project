/*
 * Created on 2005-7-28
 */
package org.lwap.init;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import org.lwap.mvc.BuiltInViewFactory;
import org.lwap.mvc.ClassViewFactory;
import org.lwap.mvc.DefaultViewFactory;
import org.lwap.mvc.ViewConfig;
import org.lwap.mvc.ViewFactoryStore;
import org.lwap.mvc.servlet.JspViewFactory;

import uncertain.core.ConfigurationError;
import uncertain.core.UncertainEngine;

/**
 * ViewFactoryStore
 * @author Zhou Fan
 * 
 */
public class ViewFactoryConfig {
    
    String 					TemplatePath;
    ServletContext 			servlet_context;
    UncertainEngine			uncertainEngine;
    
    public void onInitialize(){

        if(TemplatePath==null) throw new ConfigurationError("templatePath not set");
        ServletContext context = servlet_context.getContext(TemplatePath);
		if(context == null) throw new ConfigurationError("Can't get context "+TemplatePath);


        /* register default view builder */
        ViewFactoryStore builder_store = new ViewFactoryStore();
        
        File path = new File(context.getRealPath("/"));
        try{
        	ViewConfig   view_config = new ViewConfig(TemplatePath, path);
        	builder_store.setViewConfig(view_config);
        } catch(FileNotFoundException ex){
            throw new ConfigurationError(ex);
        }
        
        JspViewFactory   	jsp_builder = new JspViewFactory(context, TemplatePath);
        ClassViewFactory 	builtin = BuiltInViewFactory.createBuiltInViewBuilder();
        DefaultViewFactory	default_builder = new DefaultViewFactory();
        
        builder_store.registerViewFactory( jsp_builder );
        builder_store.registerViewFactory( builtin);        
        builder_store.registerViewFactory( default_builder);
        builder_store.setDefaultFactory(default_builder);
        
        uncertainEngine.getObjectSpace().registerParameter(builder_store);   
        //System.out.println("ceated ViewFactoryStore from "+TemplatePath);
        
  }


    /**
     * 
     */
    public ViewFactoryConfig(UncertainEngine uncertainEngine, ServletContext servlet_context) {
        this.uncertainEngine = uncertainEngine;
        this.servlet_context = servlet_context;
    }

}
