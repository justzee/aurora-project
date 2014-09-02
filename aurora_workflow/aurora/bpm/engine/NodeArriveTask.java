/*
 * Created on 2014-9-2 下午3:34:32
 * $Id$
 */
package aurora.bpm.engine;

import aurora.bpm.define.IFlowNode;
import aurora.bpm.define.ProcessStatus;

public class NodeArriveTask implements Runnable {
    
    InstancePath    path;
    IFlowNode       node;

    public NodeArriveTask(InstancePath path, IFlowNode node) {
        super();
        this.path = path;
        this.node = node;
    }

    @Override
    public void run() {
        //if(!ProcessEngine.isRunnable(path))
         //   return;
        node.arrive(path);
    }

}
