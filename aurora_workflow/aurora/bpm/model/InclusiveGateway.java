/*
 * Created on 2014-8-27 上午1:03:11
 * $Id$
 */
package aurora.bpm.model;

import uncertain.core.ConfigurationError;
import aurora.bpm.define.AbstractGateway;
import aurora.bpm.define.ICondition;
import aurora.bpm.define.IProcessInstance;
import aurora.bpm.define.IProcessInstancePath;
import aurora.bpm.define.ProcessStatus;

public class InclusiveGateway extends AbstractGateway {
    
    String pathIndex;
    
    public ICondition createWaitingCondition(){
        InclusiveCondition condition = new InclusiveCondition();
        for(SequenceFlow flow:getIncoming()){
            condition.addFlowToWait(flow.getId());
        }
        return condition;
    }
    
    private void addArrival(IProcessInstancePath waiting_path, SequenceFlow flow_from){
        InclusiveCondition condition = (InclusiveCondition)waiting_path.getProceedCondition();
        if(condition==null)
            throw new IllegalStateException("Path in "+waiting_path.getCurrentNode()+" is not InclusiveGateway");
        String id = flow_from.getId();
        condition.addFlowFinished(id);
    }
    
    @Override
    public void arrive(IProcessInstancePath path) {
        path.finish();
        IProcessInstance inst = path.getOwner();
        IProcessInstancePath waiting_path = inst.getPathByIndex(pathIndex);
        if(waiting_path==null){
            waiting_path = inst.createPath(this);
            waiting_path.setStatus(ProcessStatus.WAITING);
            waiting_path.setProceedCondition(createWaitingCondition());
            waiting_path.setIndex(pathIndex);
        }
        addArrival(waiting_path, path.getFlowFrom());
        if(waiting_path.getProceedCondition().isMeet(waiting_path)){
            SequenceFlow out = getOutgoing().get(0);
            waiting_path.moveTo(out.getTargetNode());
        }
    }
    
    @Override
    public void validate(){
        if(getOutgoing().size()!=1)
            throw new ConfigurationError("Gateway "+getId()+":Can have only 1 outgoing sequence flow:"+getOutgoing());
        pathIndex = "InclusiveGateway:"+getId();
    }

}
