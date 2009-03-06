/*
 * Created on 2008-12-2
 */
package org.lwap.application.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.event.ISingleEventHandle;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectSpace;

public class ServiceListenerStore implements IGlobalInstance {
    
    UncertainEngine     uncertainEngine;
    CompositeMap        childs;

    /**
     * @param uncertainEngine
     */
    public ServiceListenerStore(UncertainEngine uncertainEngine) {
        this.uncertainEngine = uncertainEngine;
    }
    
    public void addListeners( CompositeMap childs ){
        this.childs = childs;
    }
    
    public CompositeMap getListeners(){
        return childs;
    }
    
    public void onInitialize(){
        Logger  logger = uncertainEngine.getLogger();
        OCManager ocManager = uncertainEngine.getOcManager();
        ObjectSpace os = uncertainEngine.getObjectSpace();
        WebApplication application = (WebApplication)os.getParameterOfType(WebApplication.class);
        if(application==null) throw new ConfigurationError("Can't get WebApplication instance from UncertainEngine");
        IServiceListenerManager slm = application.getServiceListenerManager(); 
        
        List childs_list = childs.getChilds();
        //logger.info(childs_list.size()+" listeners defined");
        Iterator it = childs_list.iterator();
        while(it.hasNext()){
            CompositeMap child = (CompositeMap)it.next();
            Object obj = ocManager.createObject(child);
            if(obj==null){
                logger.warning("Can't create event handle instance from "+child.toXML());
                continue;
            }
            if( obj instanceof ISingleEventHandle ){
                ISingleEventHandle handle = (ISingleEventHandle)obj;
                if(handle.getEvent()==null) throw new ConfigurationError("Must set 'event' property in configuration " + child.toXML());
                slm.addEventHandle(handle);
                //logger.info("Adding listener " + handle );
            }else{
                logger.warning(obj.getClass().getName()+" does not implement ISingleEventHandle. Configuration:"+child.toXML());                
            }            
        }
        logger.info(slm.getEventHandles().size()+" listeners added");
    }

}
