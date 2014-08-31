/*
 * Created on 2014-8-21 下午11:58:24
 * $Id$
 */
package aurora.bpm.execution;

public class WorkflowEventHandle {
    
    WorkflowService wflService;
    
    public boolean isApprovable( long path_id ){
        return true;
    }
    
    public boolean hasRemainingApprove( long path_id ){
        return false;
    }
    
    public void onApproveRecordChanged( long path_id ){
        if(!isApprovable(path_id))
            return;
        if(!hasRemainingApprove(path_id))
            return;
        wflService.proceed(path_id);
    }
    
    public void onPathFinished( long workflow_id ){
        
    }

}
