/*
 * Created on 2014-8-19 上午12:24:26
 * $Id$
 */
package aurora.workflow.execution;

public class WorkflowService {
    
    class ApproveData {
        
    };
    
    private long getInstanceIDbyApproveRecord( long approve_record_id){
        return 0;
    }
    
    public void proceed( long path_id ){
        //getNextNodeID( path_id);
        /* 
         if(nextNode==null || nextNode.isEndStateNode()){
            setPathState(path_id, FINISHED);
            sendMessage("workflow.instance.controlQueue", PATH_FINISHED);
         }else{
            update path set current_node = nextNode.id;
            arrive(path_id);
         }
         
        */
    }
    
    public void arrive( long path_id ){
        
    }
    
    public void fireEvent( long instance_id, String event, Object...args ){
        
    }
    
    public void approve( long approve_record_id, ApproveData data ){
        //createApproveRecord();
        //deleteRecepientRecord();
        //sendMessage( APPROVE_RECORD_CHANGED, instance_id, path_id );
        //
    }
    
    public void terminate( long instance_id ){
        
    }
    

}
