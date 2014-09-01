/*
 * Created on 2014-8-26 下午8:31:08
 * $Id$
 */
package aurora.bpm.model;

import java.util.LinkedList;
import java.util.List;

import aurora.bpm.define.AbstractFlowElementsContainer;
import aurora.bpm.define.IFlowElement;

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
        addFlowElement(st);
    }
    
    public StartEvent getStartEvent(){
        return startEvent;
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
    
    @Override
    public void validate(){
        for(IFlowElement elm:this.getFlowElements()){
            if(elm instanceof SequenceFlow)
                elm.validate();
        }
        for(IFlowElement elm:this.getFlowElements()){
            if(! (elm instanceof SequenceFlow))
                elm.validate();
        }
    }
    
    

}
