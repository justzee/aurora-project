/*
 * Created on 2014-8-29 下午10:41:47
 * $Id$
 */
package aurora.bpm.execution;

import java.util.List;
import java.util.LinkedList;

import aurora.bpm.model.ICondition;
import aurora.bpm.model.IFlowNode;
import aurora.bpm.model.IProcessInstancePath;
import aurora.bpm.model.ProcessStatus;
import aurora.bpm.model.SequenceFlow;

public class InstancePath implements IProcessInstancePath {
    
    ProcessInstance     owner;
    IFlowNode           currentNode;
    IFlowNode           nodeFrom;
    SequenceFlow        flowFrom;
    ProcessStatus       status;
    
    ICondition          condition;
    
    protected InstancePath(ProcessInstance owner){
        this.owner = owner;
    }

    public ProcessInstance getOwner() {
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
    
    public void moveTo( IFlowNode node ){
        System.out.println(this.currentNode==null?"(null)":this.currentNode.getId()+"->"+node.getId());
        this.status = ProcessStatus.RUNNING;
        this.nodeFrom = this.currentNode;
        this.flowFrom = this.currentNode.getOutgoing().get(0);
        this.currentNode = node;
        node.arrive(this);
    }
    
    public void finish(){
        getOwner().pathFinish(this);        
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
