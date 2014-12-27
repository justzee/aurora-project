/*
 * Created on 2014年12月23日 下午2:47:59
 * $Id$
 */
package pipe.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pipe.base.IEndPoint;
import pipe.base.IFilter;
import pipe.base.IPipe;
import pipe.base.Returnable;

public class SimplePipe implements IPipe, SimplePipeMBean {

    public static final int INIT_THREAD_ARRAY_SIZE = 50;

    List<IFilter> filters;
    IEndPoint endPoint;
    String id;
    //int currentThreadCount;
    List<WorkerThread> workerThreadList;

    BlockingQueue<Object> taskQueue;
    ThreadGroup workerThreadGroup;
    SimplePipeWatcherThread watcherThread;

    /**
     * Max task limit. if task count in queue exceed this value, add() will fail
     */
    int maxTaskCount = 2000;

    /**
     * auto create new worker thread if remaining tasks in queue grow more than
     * this value
     */
    int expandCount = 995;

    /** auto release thread if tasks in queue is less than this value */
    int releaseCount = 5;

    /** won't create more threads than this */
    int maxThreads = 50;

    /** minimal threads active */
    int minThreads = 2;

    /** auto release working threads after idleTime (in ms) */
    int idleTime = 1000;

    /** If this pipe is overheat, which means task queue grow too long */
    boolean overheat = false;

    /*
     * // max threads ever created int maxThreadsCreated;
     * 
     * // time when max threads is meet Date maxThreadsTime;
     */

    boolean running = false;
    boolean shutdownInProcess = false;

    public SimplePipe(String id, int init_workers) {
        this.id = id;
        taskQueue = new LinkedBlockingQueue<Object>();
        filters = new LinkedList<IFilter>();
        //currentThreadCount = init_workers;
        this.minThreads = init_workers;
        workerThreadList = new ArrayList<WorkerThread>(INIT_THREAD_ARRAY_SIZE);
    }

    public void addFilter(IFilter filter) {
        filters.add(filter);
    }

    public boolean removeFilter(IFilter filter) {
        return filters.remove(filter);
    }

    public void addData(Object data) {
        if (data == null)
            throw new NullPointerException();
        try {
            taskQueue.put(data);
        } catch (InterruptedException ex) {

        }
    }

    public void addData(Object data, IPipe return_pipe)

    {
        Returnable r = new Returnable(data, return_pipe);
        addData(r);
    }

    public Object take() throws InterruptedException {
        if (!running)
            return null;
        return taskQueue.take();
    }

    public IEndPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(IEndPoint endPoint) {
        this.endPoint = endPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String getThreadName(int n) {
        StringBuffer name = new StringBuffer();
        name.append(id);
        name.append(".Worker.").append(n);
        return name.toString();
    }

    public int size() {
        return taskQueue.size();
    }

    protected void createWorkerThread(int id) {
        WorkerThread thread = new WorkerThread(this.workerThreadGroup,
                getThreadName(id), this);
        thread.start();
        workerThreadList.add(thread);
    }

    protected boolean expand() {
        if (this.getThreadCount() >= this.maxThreads)
            return false;
        createWorkerThread(workerThreadList.size());
        return true;
    }

    protected boolean release() {
        if (this.getThreadCount() <= this.minThreads)
            return false;
        int id = workerThreadList.size() - 1;
        WorkerThread thread = workerThreadList.get(id);
        thread.interrupt();
        workerThreadList.remove(id);
        return true;
    }

    public void start() {
        if (running)
            throw new IllegalStateException("Already started");
        if (endPoint == null)
            throw new IllegalStateException("End point not set");
        running = true;
        shutdownInProcess = false;
        endPoint.start();
        workerThreadGroup = new ThreadGroup(id + "Workers");
        for (int i = 0; i < minThreads; i++) {
            createWorkerThread(i);
        }
        watcherThread = new SimplePipeWatcherThread(this);
        watcherThread.start();

    }

    public void shutdown() {
        shutdownInProcess = true;
        watcherThread.interrupt();
        workerThreadGroup.interrupt();
        /*
         * while(workerList.size()>0){ ListIterator<WorkerThread> it =
         * workerList.listIterator(); while( it.hasNext() ){ WorkerThread worker
         * = it.next(); if(!worker.running) it.remove(); } }
         */
        endPoint.stop();
        clearUp();
    }

    private void clearUp() {

        workerThreadList.clear();
        // workerGroup.destroy();
        taskQueue.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pipe.simple.SimplePipeMBean#getThreadCount()
     */
    public int getThreadCount() {
        return workerThreadList.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pipe.simple.SimplePipeMBean#getQueueSize()
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    public int getExpandCount() {
        return expandCount;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setExpandCount(int expandCount) {
        this.expandCount = expandCount;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMinThreads(int minThreads) {
        this.minThreads = minThreads;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public int getMaxTaskCount() {
        return maxTaskCount;
    }

    public boolean getOverheat() {
        return overheat;
    }

    public void setMaxTaskCount(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
    }

    public int getReleaseCount() {
        return releaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        this.releaseCount = releaseCount;
    }

}
