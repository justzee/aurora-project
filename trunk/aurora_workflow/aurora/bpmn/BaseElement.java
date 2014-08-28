/*
 * Created on 2014-8-23 下午5:49:19
 * $Id$
 */
package aurora.bpmn;

import uncertain.ocm.AbstractLocatableObject;


public abstract class BaseElement extends AbstractLocatableObject implements IElement {

    String  id;
    
    public BaseElement(){
        
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id){
        this.id = id;
    }

}
