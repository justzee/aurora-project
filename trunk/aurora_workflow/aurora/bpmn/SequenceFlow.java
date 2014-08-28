/*
 * Created on 2014-8-27 上午12:25:40
 * $Id$
 */
package aurora.bpmn;

import uncertain.core.ConfigurationError;


public class SequenceFlow  extends FlowElement {
    
    String  sourceRef;
    String  targetRef;
    
    IFlowNode   source;
    IFlowNode   target;
    
    public String getSourceRef() {
        return sourceRef;
    }
    
    public String getTargetRef() {
        return targetRef;
    }
    
    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }
    
    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }
    
    private IFlowNode getFlowNode(String title, String id){
        IFlowElement elm = getContainer().getFlowElement(id);
        if(elm==null)
            throw new ConfigurationError(this.getOriginLocation()+":"+title+"-"+ id+" not found");
        if(elm instanceof IFlowNode){
            return (IFlowNode)elm;
        }else
            throw new ConfigurationError(this.getOriginLocation()+":"+title+"-"+ id+" can't be connected by sequenceFlow");
    }
    
    public void resolveReference(){
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
        

}
