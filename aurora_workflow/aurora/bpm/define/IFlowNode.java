/*
 * Created on 2014-8-27 上午12:46:18
 * $Id$
 */
package aurora.bpm.define;

import java.util.List;

import aurora.bpm.model.SequenceFlow;

public interface IFlowNode extends IFlowElement {
    
    public void addOutgoingSequenceFlow( SequenceFlow flow );
    
    public List<SequenceFlow> getOutgoing();
    
    public void addIncomingSequenceFlow( SequenceFlow flow );
    
    public List<SequenceFlow> getIncoming();
    
    public void arrive( IProcessInstancePath path );
    
    

}
