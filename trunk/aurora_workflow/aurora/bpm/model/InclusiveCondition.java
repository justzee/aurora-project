/*
 * Created on 2014-8-31 下午7:24:59
 * $Id$
 */
package aurora.bpm.model;

import java.util.HashSet;
import java.util.Set;

public class InclusiveCondition implements ICondition {
    
    Set<String>     path_to_merge;
    
    public InclusiveCondition(){
        path_to_merge = new HashSet<String>();
    }
    
    public void addFlowToWait(String flow_id){
        path_to_merge.add(flow_id);
    }
    
    public void addFlowFinished(String flow_id){
        path_to_merge.remove(flow_id);
    }

    @Override
    public boolean isMeet(IProcessInstancePath path) {
        return path_to_merge.size()==0;
    }

}
