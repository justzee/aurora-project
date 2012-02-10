/*
 * ClassIterateHandle.java
 *
 * Created on 2002年1月8日, 下午1:11
 */

package org.javautil.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author  Administrator
 * @version 
 */
public interface ClassIterateHandle {
    
    public void beginIterate(Class cls);
    
    public void endIterate( Class cls);
    
    public void onMethod( Class owner, Method m) ;   
   
    public void onInterface( Class owner, Class itf);
    
    public void onConstructor( Class owner, Constructor constructor);
    
    public void onField( Class owner, Field fld);
    
    

}

