/*
 * Created on 2014-8-27 上午1:03:11
 * $Id$
 */
package aurora.bpm.model;

public class InclusiveGateway extends AbstractGateway {
    
    public ICondition createWaitingCondition(){
        InclusiveCondition condition = new InclusiveCondition();
        for(SequenceFlow flow:getIncoming()){
            condition.addFlowToWait(flow.getId());
        }
        return condition;
    }
    
    private void addArrival(IProcessInstancePath waiting_path, SequenceFlow flow_from){
        InclusiveCondition condition = (InclusiveCondition)waiting_path.getProceedCondition();
        condition.addFlowFinished(flow_from.getId());
    }
    
    @Override
    public void arrive(IProcessInstancePath path) {
        path.finish();
        IProcessInstance inst = path.getOwner();
        IProcessInstancePath waiting_path = inst.getPathByNode(getId());
        if(waiting_path==null){
            waiting_path = inst.createPath(this);
            waiting_path.setStatus(ProcessStatus.WAITING);
            waiting_path.setProceedCondition(createWaitingCondition());
        }
        addArrival(waiting_path, path.getFlowFrom());
        if(waiting_path.getProceedCondition().isMeet(waiting_path)){
            SequenceFlow out = getOutgoing().get(0);
            waiting_path.moveTo(out.getTargetNode());
        }
    }

}
