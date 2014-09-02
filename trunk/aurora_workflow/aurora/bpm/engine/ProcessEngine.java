/*
 * Created on 2014-9-2 下午2:49:09
 * $Id$
 */
package aurora.bpm.engine;

import aurora.bpm.define.IProcessInstance;
import aurora.bpm.define.IProcessInstancePath;
import aurora.bpm.define.ProcessStatus;
import aurora.bpm.model.Definitions;
import aurora.bpm.model.WorkflowFactory;
import aurora.bpm.model.Process;

public class ProcessEngine {
    
    long                id;
    
    WorkflowFactory     factory;
    ExecutorQueue       queue;
    int                 queueSize;
    
    public static boolean isRunnable(IProcessInstance inst ){
        ProcessStatus status = inst.getStatus();
        return status != ProcessStatus.TERMINATED && status != ProcessStatus.FINISHED && status != ProcessStatus.ERROR;
    }
    
    public static boolean isRunnable(IProcessInstancePath path ){
        if(!isRunnable(path.getOwner()))
                return false;
        ProcessStatus status = path.getStatus();
        return status != ProcessStatus.TERMINATED && status != ProcessStatus.FINISHED && status != ProcessStatus.ERROR;
    }
    
    public synchronized long getInstanceId(){
        return ++id;
    }

    public ProcessEngine(WorkflowFactory factory, int queue_size) {
        this.queueSize = queue_size;
        this.factory = factory;
        queue = new ExecutorQueue(queue_size);
    }
    
    public IProcessInstance  createInstance(Process process){
        ProcessInstance inst = new ProcessInstance(this, process);
        inst.instance_id = getInstanceId();
        return inst;
    }
    
    public void addTask( long instance_id, Runnable task){
        int index = (int)(instance_id % queueSize);
        queue.addTask(index, task);
    }

    
    public static void main(String[] args) throws Exception {
        WorkflowFactory fact = new WorkflowFactory();
        //System.out.println(fact.ocManager.getReflectionMapper().getMappingRule(SequenceFlow.class));
        Definitions def = fact.loadFromClassPath("aurora.bpm.testcase.MyProcess");
        Process process = def.getProcess();
        ProcessEngine engine = new ProcessEngine(fact, 4);
        IProcessInstance instance = engine.createInstance(process);
        instance.start();
        System.out.println("Finished!");
    }    
    

}
