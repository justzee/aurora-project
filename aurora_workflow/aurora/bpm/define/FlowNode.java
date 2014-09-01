/*
 * Created on 2014-8-27 上午12:48:01
 * $Id$
 */
package aurora.bpm.define;

import java.util.LinkedList;
import java.util.List;

import aurora.bpm.model.SequenceFlow;

public abstract class FlowNode extends FlowElement implements IFlowNode {
    
    List<SequenceFlow>  outgoing;
    List<SequenceFlow>  incoming;
    
    public FlowNode(){
        super();
        outgoing = new LinkedList<SequenceFlow>();
        incoming = new LinkedList<SequenceFlow>();
    }

    @Override
    public void addOutgoingSequenceFlow(SequenceFlow flow) {
        if(getId().equals(flow.getSourceRef()))
            outgoing.add(flow);
    }
    
    public void addIncomingSequenceFlow( SequenceFlow flow ){
        if(getId().equals(flow.getTargetRef()))
            incoming.add(flow);    
    }
    
    @Override
    public List<SequenceFlow> getOutgoing(){
        return outgoing;
    }
    @Override
    public List<SequenceFlow> getIncoming(){
        return incoming;
    }

}
