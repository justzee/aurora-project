/*
 * Created on 2014-8-27 上午1:05:18
 * $Id$
 */
package aurora.bpm.model;

public class EndEvent extends FlowNode implements IEvent {

    @Override
    public void arrive(IProcessInstancePath path) {
        path.getOwner().finish();
    }
        

}
