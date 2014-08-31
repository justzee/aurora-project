/*
 * Created on 2014-8-23 下午4:00:50
 * $Id$
 */
package aurora.bpm.model;

public interface IFlowElement extends IElement {
    
    public String   getName();
    
    public void setName(String name);
    
    public IFlowElementsContainer getContainer();
    
    public void setContainer( IFlowElementsContainer container );
    
    //public void resolveReference();

}
