/*
 * Created on 2014-8-26 下午8:30:52
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.define.AbstractFlowElementsContainer;


public class Definitions  extends AbstractFlowElementsContainer {
    
    Process process;

    public Process getProcess() {
        return process;
    }

    public void addProcess(Process process) {
        this.process = process;
    }
    
    @Override
    public void validate(){
        process.validate();
    }
    
    

}
