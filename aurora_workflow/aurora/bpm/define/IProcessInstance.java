/*
 * Created on 2014-8-30 上午12:31:28
 * $Id$
 */
package aurora.bpm.define;

public interface IProcessInstance {
    
    public void start();
    
    public void finish();
    
    public void terminate();
    
    public ProcessStatus getStatus();
    
    public IProcessInstancePath createPath( IFlowNode starting_node );
    
    public IProcessInstancePath getPathByIndex( String index );

}
