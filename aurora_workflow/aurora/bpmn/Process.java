/*
 * Created on 2014-8-26 下午8:31:08
 * $Id$
 */
package aurora.bpmn;

import java.util.LinkedList;
import java.util.List;

import uncertain.core.ConfigurationError;
import uncertain.ocm.OCManager;


public class Process  extends AbstractFlowElementsContainer {
    
    StartEvent           startEvent;
    //List<SequenceFlow>   flowList;
    
    public Process(){
        super();
        //flowList = new LinkedList<SequenceFlow>();
    }

    
    public void addStartEvent(StartEvent st){
        if(startEvent!=null)
            throw new ConfigurationError("More than one start event");
        startEvent = st;
    }
    
    public void addEndEvent(EndEvent evt){
        addFlowElement(evt);
    }
    
    public void addUserTask( UserTask task ){
        addFlowElement(task);
    }
    
    public void addSequenceFlow(SequenceFlow flow){
        //flowList.add(flow);
        addFlowElement(flow);
    }
    
    public void addParallelGateway(ParallelGateway pgw){
        addFlowElement(pgw);
    }
    
    public void addInclusiveGateway(InclusiveGateway igw){
        addFlowElement(igw);
    }
    
    public void addScriptTask(ScriptTask task){
        addFlowElement(task);
    }
    
    public void resolveReference(){
        for(IFlowElement elm:this.getFlowElements()){
            if(elm instanceof SequenceFlow){
                SequenceFlow sf = (SequenceFlow)elm;
                sf.resolveReference();
            }
        }
    }
    
    

}
