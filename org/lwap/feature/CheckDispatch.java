/*
 * Created on 2008-12-2
 */
package org.lwap.feature;

import org.lwap.application.event.AbstractServiceHandle;
import org.lwap.application.event.SessionController;
import org.lwap.controller.MainService;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.TextParser;
import uncertain.core.ConfigurationError;
import uncertain.event.EventModel;

public class CheckDispatch extends AbstractServiceHandle {
    
    String  field;
    String  value;
    String  DispatchUrl;
    String  ApplyTo;

    /**
     * @return the dispatchUrl
     */
    public String getDispatchUrl() {
        return DispatchUrl;
    }

    /**
     * @param dispatchUrl the dispatchUrl to set
     */
    public void setDispatchUrl(String dispatchUrl) {
        DispatchUrl = dispatchUrl;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    public boolean isSameUrl( String service_name, String dispatch_url ){
            int i = dispatch_url.indexOf('?');
            String check_url = i>0?dispatch_url.substring(0,i):dispatch_url;
            return check_url.indexOf( service_name )>=0;        
    }

    public int handleEvent(int sequence, CompositeMap context,
            Object[] parameters) throws Exception {        
        if(field==null) throw new ConfigurationError("Must set 'field' property for CheckDispatch");
        if(value==null) throw new ConfigurationError("Must set 'value' property for CheckDispatch");
        
        MainService svc = MainService.getServiceInstance(context);
        String service_name = svc.getServiceName();
        // Don't do dispatch for final page
        if(svc.getServiceConfig().getBoolean(MainService.KEY_FINAL_PAGE, false))
            return EventModel.HANDLE_STOP;
        if(CompositeUtil.compareObject(context, field, value)){
            String target_url = TextParser.parse(DispatchUrl,context);            
            if(!isSameUrl(service_name, target_url)){
                SessionController sc = SessionController.createSessionController(context);
                sc.setContinueFlag(false);
                sc.setDispatchUrl(target_url);
                return EventModel.HANDLE_STOP;
            }
        }
        return EventModel.HANDLE_NORMAL;
    }

}
