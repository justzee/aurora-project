/*
 * Created on 2014-8-29 下午10:41:47
 * $Id$
 */
package aurora.bpm.engine;

import aurora.bpm.define.ICondition;
import aurora.bpm.define.IFlowNode;
import aurora.bpm.define.IProcessInstance;
import aurora.bpm.define.IProcessInstancePath;
import aurora.bpm.define.ProcessStatus;
import aurora.bpm.model.SequenceFlow;

public class InstancePath implements IProcessInstancePath {
    
    ProcessInstance     owner;
    IFlowNode           currentNode;
    IFlowNode           nodeFrom;
    SequenceFlow        flowFrom;
    ProcessStatus       status;
    
    ICondition          condition;
    String              index;
    
    public String toString(){
        StringBuffer buf = new StringBuffer();
        if(nodeFrom!=null)
            buf.append(nodeFrom).append("->");
        if(flowFrom!=null)
            buf.append("("+flowFrom.getId()+")->");
        buf.append(currentNode);
        return buf.toString();
    }
    
    public class PathRunner extends Thread {
        
        IFlowNode   nodeToRun;
        
        public PathRunner(IFlowNode node){
            nodeToRun = node;
        }
        
        @Override
        public void run(){
            nodeToRun.arrive(InstancePath.this);
        }
    }
    
    protected InstancePath(ProcessInstance owner){
        this.owner = owner;
    }

    public IProcessInstance getOwner() {
        return owner;
    }

    public IFlowNode getCurrentNode() {
        return currentNode;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setCurrentNode(IFlowNode currentNode) {
        this.currentNode = currentNode;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
    
    public void setIndex(String index){
        this.index = index;
    }
    
    public String getIndex(){
        return index;    
    }
    
    public void moveTo( IFlowNode node ){
        System.out.println(this.currentNode==null?"(null)":this.currentNode.getId()+"->"+node.getId());
        this.status = ProcessStatus.RUNNING;
        this.nodeFrom = this.currentNode;
        this.flowFrom = this.currentNode.getOutgoing().get(0);
        this.currentNode = node;
        owner.pathNodeArrive(this, node);

    }
    
    public void finish(){
        owner.pathFinish(this);        
    }
    
    public void setProceedCondition(ICondition condition){
        this.condition = condition;
    }
    
    public ICondition getProceedCondition(){
        return this.condition;
    }
     
    public SequenceFlow getFlowFrom() {
        return flowFrom;
    }
    
    public void setFlowFrom(SequenceFlow fromFlow) {
        this.flowFrom = fromFlow;
    }    

/*    
    public IFlowNode getNodeFrom() {
        return nodeFrom;
    }



    public void setNodeFrom(IFlowNode fromNode) {
        this.nodeFrom = fromNode;
    }


*/    

}
