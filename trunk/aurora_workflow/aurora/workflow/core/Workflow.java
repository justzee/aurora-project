/*
 * Created on 2014-8-18 下午8:54:37
 * $Id$
 */
package aurora.workflow.core;

import java.util.HashMap;
import java.util.Map;

import aurora.bpmn.IFlowElement;

public class Workflow {
    
    Map<String, IFlowElement>  nodes;
    
    public Workflow(){
        nodes = new HashMap<String, IFlowElement>();
    }
    
    public void addNode( IFlowElement node ){
        nodes.put(node.getId(), node);
    }
    
    public IFlowElement getNode( String name ){
        return nodes.get(name);
    }

}
