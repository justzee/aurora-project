/*
 * Created on 2007-6-27
 */
package org.lwap.plugin.quartz;

import org.quartz.JobDetail;

import uncertain.composite.CompositeMap;
import uncertain.ocm.IConfigurable;

public class LwapJobDetail extends JobDetail implements IConfigurable {
    
    Class           targetJobClass;
    String          method;
    CompositeMap    config;
    
    public LwapJobDetail(){
        super();
    }
    
    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }
    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }
    /**
     * @return the targetJobClass
     */
    public Class getTargetJobClass() {
        return targetJobClass;
    }
    /**
     * @param targetJobClass the targetJobClass to set
     */
    public void setTargetJobClass(Class targetJobClass) {
        this.targetJobClass = targetJobClass;
    }
    /* (non-Javadoc)
     * @see org.quartz.JobDetail#setJobClass(java.lang.Class)
     */
    public void setJobClass(Class jobClass) {        
        super.setJobClass(LwapJobRunner.class);
        targetJobClass = jobClass;
    }
    
    public CompositeMap getConfig(){
        return config;
    }


    public void beginConfigure(CompositeMap config) {
        this.config = config;
    }

    public void endConfigure() {
    }

}
