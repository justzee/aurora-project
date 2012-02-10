/*
 * Created on 2005-12-23
 */
package org.lwap.feature;

import javax.servlet.http.HttpSession;

import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.event.EventModel;
import uncertain.proc.IEventListener;
import uncertain.proc.ProcedureRunner;
import uncertain.util.StringSplitter;


/**
 * SessionTransfer
 * @author Zhou Fan
 * transfer object from session to service context or inverse
 * <code>
 * <session-transfer event="CreateModel" 
 * 					 sequence="pre" 
 * 					 path="/parameter" 
 * 					 fields="user_id,role_id" 
 * 					 action="read" 
 * 					 case="lower"
 * 					/>
 * </code>
 */
public class SessionTransfer implements IEventListener  {
    
    public static final String READ = "read";
    public static final String WRITE = "write"; 
    public static final String LOWER = "lower";
    public static final String UPPER = "upper";    
    
    public String		Event;
    public String		Path;
    public String		Action;
    public String		Case;
    
    String[]	fields;
    int			sequence=1;
    
/*    public int attachTo(CompositeMap m, Configuration f){
        return IFeature.NORMAL;
    }
    
    
	public void beginConfigure(CompositeMap config){
	    System.out.println("configure "+config.toXML());
	}
	
	public void endConfigure(){;}  
	*/
    public void setSequence(String seq){
        sequence = EventModel.getSequence(seq.toLowerCase());
        if(sequence<0) throw new ConfigurationError("sequence must be pre, on or post");
    } 
    
    public String getSequence(){
        return null;
    }
    
    
    public void doTransfer(HttpSession session, CompositeMap context){
        if(session==null) return;
        // get target CompositeMap
        CompositeMap m=null;
        if(Path==null)
            m = context;
        else{
	        try{ 
	            m = (CompositeMap)context.getObject(Path);
	        }catch(ClassCastException e){
	            throw new ConfigurationError("object stored in '"+Path+"' is not CompositeMap");            
	        }
	        if(m==null) m = context.createChildByTag(Path);
        }
        // transfer session fields
        if(READ.equalsIgnoreCase(Action)){
            for(int i=0; i<fields.length; i++){
                m.put(fields[i], session.getAttribute(fields[i]));
            }
        }else{
            for(int i=0; i<fields.length; i++){
                String key = LOWER.equalsIgnoreCase(Case)?fields[i].toLowerCase():fields[i];
                session.setAttribute(key,m.get(fields[i]));
            }           
        }

    }
    
    public int onEvent( ProcedureRunner runner, int seq, String event_name){
        if(Event==null) throw new ConfigurationError("event attribute not set");
        if(seq==this.sequence){
            if(event_name.equals(Event)){
                MainService svc = MainService.getServiceInstance(runner.getContext());
                HttpSession session = svc.getRequest().getSession(true);
                doTransfer(session, svc.getServiceContext());                
            }
        }
        return EventModel.HANDLE_NORMAL;
    }


    /**
     * @param fields The fields to set.
     */
    public void setFields(String fstr) {
        this.fields = StringSplitter.splitToArray(fstr,',',false);
    }
    
    public String getFields(){
        return null;
    }


    public SessionTransfer() {
    }
        
}
