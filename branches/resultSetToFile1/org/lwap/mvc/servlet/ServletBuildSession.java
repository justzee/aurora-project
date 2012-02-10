/*
 * ServletBuildSession.java
 *
 * Created on 2002年1月13日, 下午3:53
 */

package org.lwap.mvc.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.lwap.mvc.BuildSession;
import org.lwap.mvc.ViewFactoryStore;

/**
 *
 * @author  Administrator
 * @version 
 */
public class ServletBuildSession extends BuildSession{
    
    HttpServletRequest 		request;
    HttpServletResponse 	response;
    StreamResponseWrapper 	wrapper;
    ServletContext 			context;    
    PageContext				pageContext;

    /** Creates new ServletBuildSession */
/*
    public ServletBuildSession( ViewBuilderStore store, OutputStream stream, ServletContext context,  HttpServletRequest request, HttpServletResponse response) throws IOException {
        super(store, stream);
        if( stream == null)
            setOutputStream( response.getOutputStream());
        else
            wrapper = new StreamResponseWrapper(response, stream);
        this.request = request;
        this.response = response;
        this.context = context;
    }
 */   
    
    public ServletBuildSession( ViewFactoryStore store, ServletContext context,  HttpServletRequest request, HttpServletResponse response, PageContext pageContext ) throws IOException {
        //super(store, response.getWriter() );
        //super(store, pageContext.getOut() );
        this.factory_store = store;        
        this.request = request;
        this.response = response;
        this.context = context;
        this.pageContext = pageContext;
        
        if( pageContext == null) 
        	this.writer = response.getWriter();
        else{        	
            //System.out.println("Using pageContext.getOut()");
            this.writer = pageContext.getOut();  
            if(this.writer==null)
                throw new IllegalArgumentException("Can't get Writer from PageContext");
        }

    }
    
    public HttpServletRequest getRequest(){
        return request;
    }
    
    public HttpServletResponse getResponse(){
         if( wrapper == null) return response;
         else return wrapper;
    }
    
    public ServletContext getContext(){
        return context;
    }

}
