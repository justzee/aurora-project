/*
 * Created on 2014-8-23 下午5:49:19
 * $Id$
 */
package aurora.bpm.model;

import java.util.List;


public abstract class FlowElement extends BaseElement implements IFlowElement {

    String                      name;
    IFlowElementsContainer      container;
    
    public FlowElement(){
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public IFlowElementsContainer getContainer(){
        return container;
    }
    
    public void setContainer( IFlowElementsContainer container ){
        this.container = container;    
    }


}
