/**
 * Created on: 2002-11-25 15:24:12
 * Author:     zhoufan
 */
package org.lwap.mvc.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

import javax.servlet.RequestDispatcher;

import org.lwap.mvc.BuildSession;
import org.lwap.mvc.View;
import org.lwap.mvc.ViewCreationException;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public class JspView implements View {
	
	RequestDispatcher   disp;
	String				view_name;
	
	public JspView( RequestDispatcher disp, String name){
		this.disp = disp;
		this.view_name = name;
	}
	
	void printErrorContent(Writer out, String title, String content)throws IOException {
		out.write(title);
		out.write(":<br><textarea rows='6' cols='60'>");
		out.write(content);
		out.write("</textarea><br>");
	}
	
	void printStackTrace( Writer out, Throwable thr, CompositeMap model, CompositeMap view){
		try{
            if(thr.getCause()!=null) thr = thr.getCause();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Throwable t = thr.getCause();
            if(t==null) t = thr;
            t.printStackTrace(new PrintStream(stream));			
			out.write("<span style='color:red;font-size:9px'>##JSP_VIEW_ERROR##<br>");
            printErrorContent(out, "exception:", thr.getClass().getName()+" - "+thr.getMessage());
			printErrorContent(out, "Stack trace:", stream.toString());
			printErrorContent(out, "Model:", model==null?"null":model.toXML());
			printErrorContent(out, "View:", view==null?"null":view.toXML());
			out.write("</span");
		}catch(Exception ex){
		}
	}

	
	public void build(BuildSession session, CompositeMap model, CompositeMap view ) throws ViewCreationException{
        if( disp == null ) throw new ViewCreationException("Can't find jsp " + this.view_name );
		ServletBuildSession s = (ServletBuildSession)session;
        try{        	
            disp.include( s.getRequest(), s.getResponse());        
        }catch(Throwable thr){
            Writer out = session.getWriter();
            //Writer out = s.getResponse().getWriter();
            printStackTrace(out, thr, model, view);
        }
	}
	
	public String getViewName(){
		return view_name;
	}

}
