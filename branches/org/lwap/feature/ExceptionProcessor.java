/*
 * Created on 2008-11-16
 */
package org.lwap.feature;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.DynamicObject;
import uncertain.core.ConfigurationError;
import uncertain.core.IGlobalInstance;

/*
 * <exception-processor>
    <processor exceptionClass="org.lwap.application" 
               dispatchURL="fnd_no_priviledge.service"  
               handleClass="org.lwap.application.DefaultExceptionHandle" />
   </exception-processor>
 */
public class ExceptionProcessor implements IGlobalInstance {
    
    
    public static class Processor extends DynamicObject {
        
        public String getExceptionClassName(){
            String clsName = getString("exceptionclass");
            return clsName;
        }
        
        public Class getExceptionClass()
            throws ClassNotFoundException
        {
            String name = getExceptionClassName();
            if(name!=null)
                return Class.forName(name);
            else
                return null;
        }
        
        public String getDispatchURL(){
            return getString("dispatchurl");
        }
        
        public Class getHandleClass()
            throws ClassNotFoundException
        {
            String clsName = getString("handleclass");
            if(clsName!=null)
                return Class.forName(clsName);
            else
                return null;
        }       
    }
    
    Map             mProcessorMap;
    Processor       mDefaultProcessor;
    
    public ExceptionProcessor(){
        mProcessorMap = new HashMap();
    }
    
    public void addProcessor( CompositeMap record )
        throws ClassNotFoundException
    {
        Processor processor =  new Processor();
        processor.initialize(record);
        processor.getExceptionClass();
        processor.getHandleClass();
        System.out.println("Adding "+processor.getObjectContext().toXML());
        String name = processor.getExceptionClassName();
        if(name==null)
            throw new ConfigurationError("Must set 'exceptionClass' property in exception processor "+processor.getObjectContext().toXML());
        if("*".equals(name))
            mDefaultProcessor = processor;
        mProcessorMap.put(name, processor);
    }
    
    public Processor getProcessor( String exception_class_name ){
        Processor p = (Processor)mProcessorMap.get(exception_class_name);
        if(p==null) p = mDefaultProcessor;
        return p;
    }

}
