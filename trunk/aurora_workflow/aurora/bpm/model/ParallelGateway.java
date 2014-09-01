/*
 * Created on 2014-8-27 上午1:02:45
 * $Id$
 */
package aurora.bpm.model;

import uncertain.core.ConfigurationError;
import aurora.bpm.define.AbstractGateway;
import aurora.bpm.define.IProcessInstance;
import aurora.bpm.define.IProcessInstancePath;

public class ParallelGateway extends AbstractGateway {
    
    @Override
    public void arrive(IProcessInstancePath path) {
        path.finish();
        IProcessInstance instance = path.getOwner();
        for(SequenceFlow flow: this.getOutgoing()){
            instance.createPath(this).moveTo(flow.getTargetNode());
        }
    }
    
    @Override
    public void validate(){
        if(getIncoming().size()!=1)
            throw new ConfigurationError(getId()+":Can have only 1 incoming sequence flow:"+getIncoming());        
    }

}
