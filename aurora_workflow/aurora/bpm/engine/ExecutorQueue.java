/*
 * Created on 2014-9-2 下午2:49:50
 * $Id$
 */
package aurora.bpm.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecutorQueue {
    
    public class WorkerThread extends Thread {
        
        BlockingQueue<Runnable>     queueToRun;

        public WorkerThread(ThreadGroup group, String name, BlockingQueue<Runnable> queue) {
            super(group, name);
            queueToRun = queue;
        }

        public void run(){
            while(!interrupted()){
                try{
                    Runnable task = queueToRun.take();
                    task.run();
                }catch(InterruptedException ex){
                    
                }
            }
        }

    }
    
    List<Queue<Runnable>>       queueArray;
    ThreadGroup                 threadGroup;
    
    public ExecutorQueue( int queue_size ){
        threadGroup = new ThreadGroup("Executor");
        queueArray = new ArrayList<Queue<Runnable>>(queue_size);
        for(int i=0; i<queue_size; i++){
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
            queueArray.add(i,queue);
            WorkerThread thread = new WorkerThread(threadGroup, "Thread"+i, queue);
            thread.start();
        }
    }
    
    public void addTask(int queue_index, Runnable task){
        queueArray.get(queue_index).add(task);
    }

}
