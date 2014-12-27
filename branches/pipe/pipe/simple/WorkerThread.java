/*
 * Created on 2014年12月23日 下午2:22:33
 * $Id$
 */
package pipe.simple;

import java.util.Date;

import pipe.base.IFilter;
import pipe.base.IReturnable;

public class WorkerThread extends Thread {
    
    SimplePipe     owner;
    boolean        running = false;

    public WorkerThread(ThreadGroup group, String name, SimplePipe owner) {
        super(group, name);
        this.owner = owner;
    }
    
    public void run(){
        while(!interrupted() && owner.running && !owner.shutdownInProcess){
            try{
                Object payload = owner.take();
                if(payload==null) 
                    try{
                        sleep(100);
                    }catch(InterruptedException iex){
                        continue;
                    };
                boolean need_return = payload instanceof IReturnable;
                Object data = need_return? ((IReturnable)payload).getData(): payload;
                if(data==null) continue;
                try{
                    for(IFilter filter:owner.filters){
                        filter.filt(data);
                    }
                    owner.endPoint.process(data);
                    if(need_return){
                        ((IReturnable)payload).getReturnPipe().addData(data);
                    }
                }catch(Throwable thr){
                    thr.printStackTrace();
                }
            }catch(InterruptedException ex){
                break;
            }
        }
        running = false;
    }
    
    public void start(){
        running = true;
        super.start();
    }
    
    


}
