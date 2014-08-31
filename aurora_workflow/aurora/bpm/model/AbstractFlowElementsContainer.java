/*
 * Created on 2014-8-27 下午12:05:13
 * $Id$
 */
package aurora.bpm.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uncertain.core.ConfigurationError;

public class AbstractFlowElementsContainer extends BaseElement implements IFlowElementsContainer {

    Map<String, IFlowElement>  nodes;
    
    public AbstractFlowElementsContainer(){
        super();
        nodes = new HashMap<String, IFlowElement>();
    }
    
    public void addFlowElement( IFlowElement node ){
        if(node.getId()==null || node.getId().length()==0)
            throw new ConfigurationError("Node must has id");
        if(nodes.containsKey(node.getId()))
            throw new ConfigurationError("Id "+node.getId()+" already exists");
        node.setContainer(this);
        nodes.put(node.getId(), node);
    }
    
    
    public IFlowElement getFlowElement( String id ){
        return nodes.get(id);
    }
    
    public Collection<IFlowElement> getFlowElements(){
        return nodes.values();
    }

}
