/*
 * Created on 2008-12-2
 */
package org.lwap.application.event;

import java.util.Iterator;
import java.util.List;

import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;
import uncertain.core.UncertainEngine;
import uncertain.event.ISingleEventHandle;
import uncertain.logging.ILogger;
import uncertain.logging.LoggingContext;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;

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
        OCManager ocManager = uncertainEngine.getOcManager();
        IObjectRegistry os = uncertainEngine.getObjectRegistry();
        ILogger logger = LoggingContext.getLogger(WebApplication.LWAP_APPLICATION_LOGGING_TOPIC, os);
        //os.getInstanceOfType(ILoggerProvider)
        WebApplication application = (WebApplication)os.getInstanceOfType(WebApplication.class);
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
