/*
 * Created on 2014-8-27 上午12:25:40
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.define.FlowElement;
import aurora.bpm.define.IFlowElement;
import aurora.bpm.define.IFlowNode;
import uncertain.composite.CompositeMap;
import uncertain.core.ConfigurationError;


public class SequenceFlow  extends FlowElement {
    
    String  sourceRef;
    String  targetRef;
    
    IFlowNode   source;
    IFlowNode   target;
    
    public String getSourceRef() {
        return sourceRef;
    }
    
    
    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }    
    
    public String getTargetRef() {
        return targetRef;
    }
    
    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }
    
    private IFlowNode getFlowNode(String title, String id){
        IFlowElement elm = getContainer().getFlowElement(id);
        if(elm==null)
            throw new ConfigurationError("Line "+this.getOriginLocation().getStartLine()+":"+title+"-"+ id+" not found");
        if(elm instanceof IFlowNode){
            return (IFlowNode)elm;
        }else
            throw new ConfigurationError("Line "+this.getOriginLocation().getStartLine()+":"+title+"-"+ id+" can't be connected by sequenceFlow");
    }
    
    public void validate(){
        //System.out.println(this+"sourceRef:"+sourceRef+" targetRef:"+targetRef);
        source = getFlowNode("source", sourceRef);
        source.addOutgoingSequenceFlow(this);
        target = getFlowNode("target", targetRef);
        target.addIncomingSequenceFlow(this);
        
    }
    
    public IFlowNode getSourceNode(){
        return source;
    }
    
    public IFlowNode getTargetNode(){
        return target;
    }
/*
    
    @Override
    public void beginConfigure(CompositeMap source){
        super.beginConfigure(source);
        System.out.println("beginConfigure"+ source.toXML());
    }
    
    @Override
    public void endConfigure(){
        super.endConfigure();
        System.out.println(this+" endConfigure, sourceRef="+this.getSourceRef());
    }
*/        

}
