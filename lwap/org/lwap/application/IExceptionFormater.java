/*
 * Created on 2007-6-6
 */
package org.lwap.application;

import uncertain.composite.CompositeMap;

public interface IExceptionFormater {
    
    public String   getMessage( Throwable exception, CompositeMap context);

}
