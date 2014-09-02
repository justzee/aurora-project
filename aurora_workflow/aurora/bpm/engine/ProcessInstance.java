/*
 * Created on 2014-8-29 下午10:40:41
 * $Id$
 */
package aurora.bpm.engine;

import java.util.LinkedList;
import java.util.List;

import aurora.bpm.define.IFlowNode;
import aurora.bpm.define.IProcessInstance;
import aurora.bpm.define.IProcessInstancePath;
import aurora.bpm.define.ProcessStatus;
import aurora.bpm.model.Process;
import aurora.bpm.model.SequenceFlow;
import aurora.bpm.model.StartEvent;

public class ProcessInstance implements IProcessInstance {
    
    Process             process;
    ProcessInstance     parent;
    List<InstancePath>  activePaths;
    ProcessStatus       status;
    long                instance_id;
    
    ProcessEngine       engine;

    public ProcessInstance(ProcessEngine engine, Process process) {
        super();
        this.engine = engine;
        this.process = process;
        activePaths = new LinkedList<InstancePath>();
    }

    public ProcessInstance(ProcessEngine engine, Process process, ProcessInstance parent) {
        super();
        this.engine = engine;
        this.process = process;
        this.parent = parent;
    }
    
    public IProcessInstancePath createPath( IFlowNode current_node ){
        InstancePath path = new InstancePath(this);
        path.currentNode = current_node;
        path.status = ProcessStatus.RUNNING;
        activePaths.add(path);
        return path;
    }
    
    public void pathFinish(InstancePath path){
        activePaths.remove(path);
        if(activePaths.size()==0)
            finish();
    }
    
    public void finish(){
        status = ProcessStatus.FINISHED;
    }
    
    public void start(){
        StartEvent ste = process.getStartEvent();
        status = ProcessStatus.RUNNING;
        for(SequenceFlow f:ste.getOutgoing()){
            InstancePath path = (InstancePath)createPath(ste);
            path.moveTo(f.getTargetNode());            
        }        
    }
    
    public void terminate(){
        activePaths.clear();
        status = ProcessStatus.TERMINATED;
    }
    
    public IProcessInstancePath getPathByIndex( String index ){
        for(InstancePath path:activePaths){
            if(index.equals(path.getIndex())){
                return path;
            }            
        }
        return null;
    }
    
    public void pathNodeArrive( InstancePath path, IFlowNode node ){
        engine.addTask(instance_id, new NodeArriveTask(path,node));
    }
    
    public ProcessStatus getStatus(){
        return status;
    }
    
    
}
