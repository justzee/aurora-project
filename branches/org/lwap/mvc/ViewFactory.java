/*
 * Transformer.java
 *
 * Created on 2002年1月12日, 下午10:42
 */

package org.lwap.mvc;

import uncertain.composite.CompositeMap;

/**
 *
 * @author  Administrator
 * @version 
 */
public interface ViewFactory {

    public View createView(BuildSession session, String view_name, CompositeMap model, CompositeMap view ) throws ViewCreationException;
    
    public String getNamespaceURL();
    
    public void setViewFactoryStore(ViewFactoryStore store);

    public ViewFactoryStore getViewFactoryStore();    
    
//    public CompositeMap createComponent( String name,  CompositeMap  props);
    
}
