/*
 * Created on 2008-12-1
 */
package org.lwap.application.event;

import uncertain.composite.CompositeMap;
import uncertain.event.AbstractEventHandle;
import uncertain.ocm.IObjectRegistry;

public abstract class AbstractServiceHandle extends AbstractEventHandle {
    
    
    public AbstractServiceHandle(){
        super();
    }

    public abstract int handleEvent(int sequence, CompositeMap context,
            Object[] parameters) throws Exception ;

}
