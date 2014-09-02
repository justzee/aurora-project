/*
 * Created on 2014-8-27 上午1:05:18
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.define.FlowNode;
import aurora.bpm.define.IEvent;
import aurora.bpm.define.IProcessInstancePath;

public class EndEvent extends FlowNode implements IEvent {

    @Override
    public void arrive(IProcessInstancePath path) {
        path.getOwner().finish();
        System.out.println("End event reached");
    }
    
    @Override
    public void validate(){
        
    }
        

}
