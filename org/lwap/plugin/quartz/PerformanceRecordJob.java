/*
 * Created on 2006-11-25
 */
package org.lwap.plugin.quartz;

import org.lwap.feature.PerformanceModel;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import uncertain.core.UncertainEngine;
import uncertain.ocm.IObjectRegistry;

public class PerformanceRecordJob implements Job {

    public PerformanceRecordJob() {
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        UncertainEngine engine = SchedulerConfig.getUncertainEngine(context.getJobDetail().getJobDataMap());
        try{
            PerformanceModel model = (PerformanceModel)engine.getObjectCreator().createInstance(PerformanceModel.class);
            model.save();
            System.out.println("records saved at "+ new java.util.Date());
        }catch(Exception ex){
            ex.printStackTrace();
            throw new JobExecutionException(ex);
        }
    }

}
