/*
 * Created on 2014-8-27 上午12:55:18
 * $Id$
 */
package aurora.bpm.model;

import aurora.bpm.define.Activity;
import aurora.bpm.define.IProcessInstancePath;
import aurora.bpm.define.ProcessStatus;

public class UserTask extends Activity {
    
    @Override
    public void arrive(IProcessInstancePath path) {
        // wait for user approve
        path.setStatus(ProcessStatus.WAITING);
        // create pending approve records
        
        /* this is for demo */
        long time = (long)(1000*Math.random());
        try{
            Thread.sleep(time);
        }catch(InterruptedException ex){
            
        }
        System.out.println("Workflow approved in "+time+" ms");
        path.moveTo(this.getOutgoing().get(0).getTargetNode());
    }
    
    @Override
    public void validate(){
        
    }
        

}
