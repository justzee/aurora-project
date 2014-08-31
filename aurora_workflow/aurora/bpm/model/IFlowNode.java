/*
 * Created on 2014-8-27 上午12:46:18
 * $Id$
 */
package aurora.bpm.model;

import java.util.List;

public interface IFlowNode extends IFlowElement {
    
    public void addOutgoingSequenceFlow( SequenceFlow flow );
    
    public List<SequenceFlow> getOutgoing();
    
    public void addIncomingSequenceFlow( SequenceFlow flow );
    
    public List<SequenceFlow> getIncoming();
    
    public void arrive( IProcessInstancePath path );
    
    

}
