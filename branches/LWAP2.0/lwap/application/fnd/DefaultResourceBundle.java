/*
 * Created on 2005-10-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * @author Jian
 *
 */
public class DefaultResourceBundle extends ResourceBundle {
    
    HashMap resources = new HashMap();
    

    /* (non-Javadoc)
     * @see java.util.ResourceBundle#getKeys()
     */
    public  Enumeration getKeys(){
        return new Enumeration(){
            private Object next ;
            private Iterator i = resources.keySet().iterator();
            public boolean hasMoreElements() {
                if (next == null){
                    if (i.hasNext()){
                        next = i.next();
                    }
                }
                return next != null;
            }

            public Object nextElement() {
                if (hasMoreElements()) {
                    Object result = next;
                    next = null;
                    return result;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    /* (non-Javadoc)
     * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
     */
    public Object handleGetObject(String key){    
         		Object obj = resources.get(key.toUpperCase());
                return obj==null? key:obj;
    }
    
    public void putString( String key, String value){          
                resources.put(key,value);
    }

}
