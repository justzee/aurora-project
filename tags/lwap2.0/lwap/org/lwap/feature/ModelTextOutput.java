/*
 * Created on 2005-10-30
 */
package org.lwap.feature;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;


/**
 * XmlOutput
 * @author Zhou Fan
 * 
 */
public abstract class ModelTextOutput implements IFeature {
    
    public String content;
    public String contentType;


    public ModelTextOutput() {
        
    }    
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    
    public int attachTo(CompositeMap config, Configuration procConfig) {   
        return IFeature.NORMAL;
    }
    
    public int onCreateView(ProcedureRunner runner){
        runner.getContext().put(MainService.KEY_VIEW_OUTPUT, new Boolean(true));
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
    }
    
    public abstract String getDefaultContentType();
    
    public abstract void writeOutput( HttpServletResponse response,CompositeMap model) 
        throws IOException;
    /*
    public void onBuildOutputContent(ProcedureRunner runner) throws IOException{
        CompositeMap	context = runner.getContext();
        MainService		service = MainService.getServiceInstance(context);
        runner.setHandleFlag(EventModel.HANDLE_NO_SAME_SEQUENCE);
        CompositeMap	model = service.getModel();
        HttpServletResponse response = service.getResponse();
        
        Object output = null;
        if(content!=null){
            output = model.getObject(content);
        }
        else
            output = model;
        if(output==null) return;
        response.setContentType(contentType==null?getDefaultContentType():contentType);
        if(output instanceof CompositeMap){
            writeOutput(response, (CompositeMap)output);
        }else{
            response.getWriter().write(output.toString());
        }        
        runner.setHandleFlag(EventModel.HANDLE_NO_SAME_SEQUENCE);
    }
    */
    
    public int onBuildOutputContent(ProcedureRunner runner) throws IOException{
        CompositeMap    context = runner.getContext();
        MainService     service = MainService.getServiceInstance(context);
        CompositeMap    model = service.getModel();
        HttpServletResponse response = service.getResponse();
        
        Object output = null;
        if(content!=null){
            output = model.getObject(content);
        }
        else
            output = model;
        if(output==null) return EventModel.HANDLE_NO_SAME_SEQUENCE;
        response.setContentType(contentType==null?getDefaultContentType():contentType);
        if(output instanceof CompositeMap){
            writeOutput(response, (CompositeMap)output);
        }else{
            response.getWriter().write(output.toString());
        }        
        return EventModel.HANDLE_NO_SAME_SEQUENCE;
    }
    
}
