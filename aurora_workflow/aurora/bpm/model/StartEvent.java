/*
 * Created on 2014-8-27 上午1:04:22
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.define.FlowNode;
import aurora.bpm.define.IEvent;
import aurora.bpm.define.IProcessInstancePath;

public class StartEvent extends FlowNode implements IEvent {

    @Override
    public void arrive(IProcessInstancePath path) {
        // do nothing
    }
    
    @Override
    public void validate(){
        
    }
    
    

}
