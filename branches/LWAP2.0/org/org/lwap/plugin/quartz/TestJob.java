/*
 * Created on 2006-11-24
 */
package org.lwap.plugin.quartz;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;

/**
 * A demo class that implements Job interface and can be scheduled by quartz
 * @author Zhou Fan
 *
 */
public class TestJob implements Job{
    
    static int id = 0;
    
    int instance_id;

    public TestJob(){
        instance_id = id++;
    }
    
    public void execute(JobExecutionContext context)  
    throws JobExecutionException{
        System.out.print("Test job instance "+instance_id);
        System.out.println(" called at " + new Date());
    }
    
  
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
        SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

        Scheduler sched = schedFact.getScheduler();
        
        JobDetail jobDetail = new JobDetail(
                "myJob", 
                Scheduler.DEFAULT_GROUP, 
                TestJob.class);
        
        JobDataMap map = jobDetail.getJobDataMap();
        map.put("data", new Date());
        
        SimpleTrigger trigger = new SimpleTrigger("myTrigger",
                Scheduler.DEFAULT_GROUP,
                new Date(),
                null,
                SimpleTrigger.REPEAT_INDEFINITELY,
                2L * 1000L);
        
        sched.scheduleJob(jobDetail, trigger);
        sched.start();
        
    }

}
