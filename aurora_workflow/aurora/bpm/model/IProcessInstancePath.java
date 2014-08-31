/*
 * Created on 2014-8-30 上午12:31:51
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.execution.ProcessInstance;

public interface IProcessInstancePath {
    
    public ProcessInstance getOwner();

    public IFlowNode getCurrentNode();

    public ProcessStatus getStatus();

    public void setStatus(ProcessStatus status);
    
    public void moveTo( IFlowNode node );
    
    public void finish();
    
    public void setProceedCondition(ICondition condition);
    
    public ICondition getProceedCondition();
    
    public SequenceFlow getFlowFrom();

    public void setFlowFrom(SequenceFlow fromFlow);    
    
    //public void setWaitingFlow( Set<String> flow_id_set);

/*    
    public void setCurrentNode(IFlowNode currentNode);

    public IFlowNode getNodeFrom();

    public void setNodeFrom(IFlowNode fromNode);

    public SequenceFlow getFlowFrom();

    public void setFlowFrom(SequenceFlow fromFlow);
*/    


    

}
