/*
 * Created on 2007-6-27
 */
package org.lwap.plugin.quartz;

import java.lang.reflect.Method;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import uncertain.core.UncertainEngine;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectSpace;

public class LwapJobRunner implements Job {

    public static final String DEFAULT_METHOD_NAME = "run";

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        UncertainEngine engine = SchedulerConfig.getUncertainEngine(context.getJobDetail().getJobDataMap());
        ObjectSpace os = engine.getObjectSpace();
        LwapJobDetail detail = (LwapJobDetail) context.getJobDetail();
        Class cls_to_run = detail.getTargetJobClass();
        String method = detail.getMethod();
        if(method==null) method=DEFAULT_METHOD_NAME;
        try{
            // Get method to run
            Method[] marray = cls_to_run.getMethods();
            Method job_method = null;
            for(int i=0; i<marray.length; i++)
                if(marray[i].getName().equals(method)){
                    job_method=marray[i];
                    break;
                }
            if(job_method==null) 
                throw new JobExecutionException("'method' property not set in job detail config");
            // Try to construct all parameters 
            Class[] types = job_method.getParameterTypes();
            Object[] args = new Object[types.length];
            for(int i=0; i<types.length; i++){
                if(JobExecutionContext.class.equals(types[i]))
                    args[i] = context;
                else    
                    args[i] = os.getParameterOfType(types[i]);
            }
            Object instance = os.createInstance(cls_to_run);
            OCManager om = engine.getOcManager();
            om.populateObject(detail.getConfig(), instance);
            job_method.invoke(instance, args);
        }catch(Exception ex){
            throw new JobExecutionException(ex);
        }
    }


}
