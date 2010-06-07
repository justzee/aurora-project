/*
 * Created on 2008-12-1
 */
package org.lwap.application.event;

import java.util.Collection;

import uncertain.event.Configuration;
import uncertain.event.ISingleEventHandle;

public interface IServiceListenerManager {
    
    public void addEventHandle( ISingleEventHandle listener );
    
    public void addEventHandles( Collection listener_list );
    
    public void removeEventHandle( ISingleEventHandle  listener );    
    
    public Collection getEventHandles();
    
    /** Add all listeners to configuration */
    public void populateConfiguration( Configuration config );

}
