/*
 * Created on 2008-12-1
 */
package org.lwap.application.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uncertain.event.Configuration;
import uncertain.event.ISingleEventHandle;

public class BasicServiceListenerManager implements IServiceListenerManager {
    
    Set        mListenerSet;
    
    public BasicServiceListenerManager(){
        mListenerSet = new HashSet();
    }

    public void addEventHandle(ISingleEventHandle listener) {
        mListenerSet.add(listener);
    }

    public void addEventHandles(Collection listener_list) {
        mListenerSet.addAll(listener_list);
    }

    public void removeEventHandle(ISingleEventHandle listener) {
        mListenerSet.remove(listener);
    }
    
    public Collection getEventHandles(){
        return mListenerSet;
    }
    
    public void populateConfiguration( Configuration config ){
        if(mListenerSet.size()==0) return;
        Iterator it = mListenerSet.iterator();
        while(it.hasNext()){
            ISingleEventHandle handle = (ISingleEventHandle)it.next();
            config.getHandleManager().addSingleEventHandle(handle,false);
        }
    }

}
