/*
 * Created on 2014-8-23 下午5:49:19
 * $Id$
 */
package aurora.bpm.model;

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
    
    public String toString(){
        String name = this.getClass().getName();
        String s = name.substring(name.lastIndexOf('.')+1);
        return s+"<"+getId()+">";
    }

}
