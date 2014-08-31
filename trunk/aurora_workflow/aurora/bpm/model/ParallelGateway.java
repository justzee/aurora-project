/*
 * Created on 2014-8-27 上午1:02:45
 * $Id$
 */
package aurora.bpm.model;

public class ParallelGateway extends AbstractGateway {
    
    @Override
    public void arrive(IProcessInstancePath path) {
        path.finish();
        IProcessInstance instance = path.getOwner();
        for(SequenceFlow flow: this.getOutgoing()){
            instance.createPath(this).moveTo(flow.getTargetNode());
        }
    }

}
