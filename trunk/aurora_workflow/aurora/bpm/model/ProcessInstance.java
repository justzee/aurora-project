/*
 * Created on 2014-8-29 下午10:40:41
 * $Id$
 */
package aurora.bpm.model;

import java.util.LinkedList;
import java.util.List;

import aurora.bpm.define.IFlowNode;
import aurora.bpm.define.IProcessInstance;
import aurora.bpm.define.IProcessInstancePath;
import aurora.bpm.define.ProcessStatus;

public class ProcessInstance implements IProcessInstance {
    
    Process             process;
    ProcessInstance     parent;
    List<InstancePath>  activePaths;
    ProcessStatus       status;

    public ProcessInstance(Process process) {
        super();
        this.process = process;
        activePaths = new LinkedList<InstancePath>();
    }

    public ProcessInstance(Process process, ProcessInstance parent) {
        super();
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
    
    public IProcessInstancePath getPathByNode( String node_id ){
        if(node_id==null)
            throw new NullPointerException("node_id is null");
        for(InstancePath path:activePaths){
            if(node_id.equals(path.getCurrentNode().getId())){
                return path;
            }            
        }
        return null;
    }
    
    
}
